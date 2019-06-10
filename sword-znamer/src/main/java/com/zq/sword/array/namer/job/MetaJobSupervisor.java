package com.zq.sword.array.namer.job;

import com.zq.sword.array.namer.piper.MetaPiper;
import com.zq.sword.array.namer.piper.MetaPiperGroup;
import com.zq.sword.array.namer.piper.MetaPiperListener;
import com.zq.sword.array.namer.piper.MetaPiperSupervisor;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobType;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.tasks.TaskExecutorPool;
import com.zq.sword.array.tasks.TimedTaskExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: 元Job管理器
 * @author: zhouqi1
 * @create: 2019-06-10 10:39
 **/
public class MetaJobSupervisor {

    /**
     * piper系统
     */
    private MetaPiperSupervisor metaPiperSupervisor;

    /**
     * Job清单数据集合
     */
    private Map<String, NameJob> nameJobs;

    /**
     * key->jobName value->MetaJob
     */
    private Map<String, MetaJob> jobs;

    /**
     * JobCommand 队列
     */
    private Map<String, LinkedBlockingQueue<JobCommand>> commands;

    /**
     * 定时任务执行器
     */
    private TimedTaskExecutor timedTaskExecutor;


    public MetaJobSupervisor(MetaPiperSupervisor metaPiperSupervisor) {
        this.metaPiperSupervisor = metaPiperSupervisor;
        this.metaPiperSupervisor.registerListener(new JobHandler());
        nameJobs = new ConcurrentHashMap<>();
        jobs = new HashMap<>();
        this.commands = new ConcurrentHashMap<>();
        this.timedTaskExecutor = TaskExecutorPool.getCommonTimedTaskExecutor();

        //设置定时任务执行
        initTimedTasks();
    }

    /**
     * 初始化定时任务
     */
    private void initTimedTasks() {

        //定时扫描所有的job任务 根据状态 进行命令创建
        timedTaskExecutor.timedExecute(()->{
            Collection<MetaJob> metaJobs = allMetaJobs();
            if(metaJobs != null && !metaJobs.isEmpty()){
                for (MetaJob metaJob : metaJobs){
                    Collection<BranchJob> branchJobs = metaJob.allBranchJobs();
                    if(branchJobs != null && !branchJobs.isEmpty()){
                        for (BranchJob branchJob : branchJobs){
                            MetaPiper piper = branchJob.getPiper();
                            LinkedBlockingQueue<JobCommand> commandQueue = commands.get(piper.location());
                            if(commandQueue == null){
                                commandQueue = new LinkedBlockingQueue<>();
                                commands.put(piper.location(), commandQueue);
                            }
                            int jobState = branchJob.getJobState();
                            if(jobState == JobHealth.EXCEPTION && branchJob.getJobResetCount().get() < 3){
                                commandQueue.offer(new JobCommand(JobType.JOB_RESTART.getType(), branchJob.getName()));
                            }
                        }
                    }
                }
            }

        }, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * 得到指定piper的JobCommand
     * @param namePiper
     * @return
     */
    public JobCommand getJobCommand(NamePiper namePiper) {
        synchronized (commands){
            LinkedBlockingQueue<JobCommand> commandQueue = commands.get(namePiper.getId());
            if(commandQueue != null){
                return commandQueue.poll();
            }
        }
        return null;
    }

    /**
     * 上报Job健康状态
     * @param jobHealth
     */
    public void reportJobHealth(JobHealth jobHealth) {
        MetaJob metaJob = getMetaJob(jobHealth.getName());
        if(metaJob != null){
            BranchJob job = metaJob.getBranchJob(jobHealth.getGroup());
            job.setJobState(jobHealth.getState());
            job.setJobEx(jobHealth.getEx());

        }
    }

    /**
     *  创建Job
     * @param nameJob
     */
    public void createJob(NameJob nameJob) {
        nameJobs.put(nameJob.getName(), nameJob);
    }

    /**
     *  创建Job
     * @param jobName
     */
    public void startJob(String jobName) {
        NameJob nameJob = nameJobs.get(jobName);
        if(nameJob == null){
            return;
        }
        Map<String, String> sourceRedises = nameJob.getSourceRedis();
        MetaJob metaJob = new MetaJob();
        metaJob.setName(jobName);
        metaJob.setSourceRedis(sourceRedises);
        List<MetaPiper> selectNamePipers = new ArrayList<>();
        if(sourceRedises != null && !sourceRedises.isEmpty()){
            for(String piperGroup : sourceRedises.keySet()){
                String sourceRedis = sourceRedises.get(piperGroup);
                MetaPiperGroup group = metaPiperSupervisor.getPiperGroup(piperGroup);
                Collection<MetaPiper> pipers = group.allPipers();
                if(pipers != null && !pipers.isEmpty()){
                    MetaPiper piper = selectPiper(pipers, sourceRedis);

                    BranchJob branchJob = new BranchJob(jobName, piperGroup, sourceRedis);
                    branchJob.setPiper(piper);

                    for(MetaPiper p : pipers){
                        if(!p.location().equalsIgnoreCase(piper.location())){
                            branchJob.addBackupPiper(p);
                        }
                    }

                    metaJob.addBranchJob(branchJob);

                    selectNamePipers.add(piper);
                }

            }

            Collection<BranchJob> branchJobs = metaJob.getBranchJobs();
            for(BranchJob branchJob : branchJobs){
                for(MetaPiper p : selectNamePipers){
                    if(!p.location().equalsIgnoreCase(branchJob.getPiper().location())){
                        branchJob.addConsumePiper(p);
                    }
                }
            }

            for(BranchJob branchJob : branchJobs){
                MetaPiper piper = branchJob.getPiper();
                LinkedBlockingQueue<JobCommand> commandQueue = commands.get(piper.location());
                if(commandQueue == null){
                    commandQueue = new LinkedBlockingQueue<>();
                    commands.put(piper.location(), commandQueue);
                }
                commandQueue.add(new JobCommand(JobType.JOB_NEW.getType(), branchJob.getName(), branchJob.getPiperGroup(), branchJob.getSourceRedis(),
                        branchJob.getBackupPipers().stream().map(c->c.location()).collect(Collectors.toList()),
                        branchJob.getConsumePipers().stream().map(c->c.location()).collect(Collectors.toList())));
            }
        }
    }


    /**
     *
     * @param pipers
     * @param sourceRedis
     * @return
     */
    private MetaPiper selectPiper(Collection<MetaPiper> pipers, String sourceRedis) {
        return pipers.iterator().next();
    }

    /**
     * 删除任务
     * @param jobName
     */
    public void removeJob(String jobName) {
        nameJobs.remove(jobName);
        jobs.remove(jobName);

    }

    /**
     * 得到所有的MetaJob
     * @return
     */
    private Collection<MetaJob> allMetaJobs() {
        return Collections.unmodifiableCollection(jobs.values());
    }

    /**
     * 通过jobName得到MetaJob
     * @param jobName
     * @return
     */
    private MetaJob getMetaJob(String jobName) {
        return jobs.get(jobName);
    }


    /**
     *
     */
    private class JobHandler implements MetaPiperListener {

        @Override
        public void add(NamePiper piper) {

        }

        @Override
        public void remove(NamePiper piper) {

        }
    }
}
