package com.zq.sword.array.namer.piper;

import com.zq.sword.array.namer.piper.job.BranchJob;
import com.zq.sword.array.namer.piper.job.MainJob;
import com.zq.sword.array.namer.piper.job.MainJobSystem;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobType;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.network.rpc.protocol.processor.NamerServiceProcessor;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: piper监控器
 * @author: zhouqi1
 * @create: 2019-04-28 20:12
 **/
public class PiperServerController implements NamerServiceProcessor {

    /**
     * piper系统
     */
    private NamePiperSystem namePiperSystem;

    /**
     * 任务监控器
     */
    private MainJobSystem mainJobSystem;

    /**
     * JobCommand 队列
     */
    private Map<Long, LinkedBlockingQueue<JobCommand>> commands;

    /**
     * 定时任务执行器
     */
    private TimedTaskExecutor timedTaskExecutor;

    public PiperServerController() {
        this.namePiperSystem = new NamePiperSystem();
        this.mainJobSystem = new MainJobSystem();
        this.commands = new ConcurrentHashMap<>();
        this.timedTaskExecutor = new SingleTimedTaskExecutor();

        //设置定时任务执行
        initTimedTasks();
    }

    /**
     * 初始化定时任务
     */
    private void initTimedTasks() {

        //定时扫描所有的job任务 根据状态 进行命令创建
        timedTaskExecutor.timedExecute(()->{
            Collection<MainJob> mainJobs = mainJobSystem.allMainJobs();
            if(mainJobs != null && !mainJobs.isEmpty()){
                for (MainJob mainJob : mainJobs){
                    Collection<BranchJob> branchJobs = mainJob.allBranchJobs();
                    if(branchJobs != null && !branchJobs.isEmpty()){
                        for (BranchJob branchJob : branchJobs){
                            NamePiper piper = branchJob.getPiper();
                            LinkedBlockingQueue<JobCommand> commandQueue = commands.get(piper.getId());
                            if(commandQueue == null){
                                commandQueue = new LinkedBlockingQueue<>();
                                commands.put(piper.getId(), commandQueue);
                            }
                            int jobState = branchJob.getJobState();
                            if(jobState == JobHealth.EXCEPTION && branchJob.getJobResetCount().get() < 3){
                                commandQueue.offer(new JobCommand(JobType.JOB_RESTART.getType(), branchJob.getName()));
                            }

                            if(jobState == JobHealth.NEW){
                                commandQueue.offer(new JobCommand(JobType.JOB_START.getType(), branchJob.getName()));
                            }
                        }
                    }
                }
            }

        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handlePiperRegister(NamePiper namePiper) {
        namePiperSystem.addPiper(namePiper);
    }

    @Override
    public JobCommand handleJobCommandReq(NamePiper namePiper) {
        synchronized (commands){
            LinkedBlockingQueue<JobCommand> commandQueue = commands.get(namePiper.getId());
            if(commandQueue != null){
                return commandQueue.poll();
            }
        }
        return null;
    }

    @Override
    public void handleTaskHealthReport(JobHealth jobHealth) {
        MainJob mainJob = mainJobSystem.getMainJob(jobHealth.getName());
        if(mainJob != null){
            BranchJob job = mainJob.getBranchJob(jobHealth.getGroup());
            job.setJobState(jobHealth.getState());
            job.setJobEx(jobHealth.getEx());
        }
    }

    @Override
    public void handleClientStartJobReq(NameJob nameJob) {
        String jobName = nameJob.getName();
        Map<String, String> sourceRedises = nameJob.getSourceRedis();
        MainJob mainJob = new MainJob();
        mainJob.setName(jobName);
        mainJob.setSourceRedis(sourceRedises);
        List<NamePiper> selectNamePipers = new ArrayList<>();
        if(sourceRedises != null && !sourceRedises.isEmpty()){
            for(String piperGroup : sourceRedises.keySet()){
                String sourceRedis = sourceRedises.get(piperGroup);
                List<NamePiper> pipers = namePiperSystem.getGroupPipers(piperGroup);
                if(pipers != null && !pipers.isEmpty()){
                    NamePiper piper = selectPiper(pipers, sourceRedis);

                    BranchJob branchJob = new BranchJob(jobName, piperGroup, sourceRedis);
                    branchJob.setPiper(piper);

                    for(NamePiper p : pipers){
                        if(p.getId() != piper.getId()){
                            branchJob.addBackupPiper(p);
                        }
                    }

                    mainJob.addBranchJob(branchJob);

                    selectNamePipers.add(piper);
                }

            }

            Collection<BranchJob> branchJobs = mainJob.getBranchJobs();
            for(BranchJob branchJob : branchJobs){
                for(NamePiper p : selectNamePipers){
                    if(p.getId() != branchJob.getPiper().getId()){
                        branchJob.addConsumePiper(p);
                    }
                }
            }

            for(BranchJob branchJob : branchJobs){
                NamePiper piper = branchJob.getPiper();
                LinkedBlockingQueue<JobCommand> commandQueue = commands.get(piper.getId());
                if(commandQueue == null){
                    commandQueue = new LinkedBlockingQueue<>();
                    commands.put(piper.getId(), commandQueue);
                }
                commandQueue.add(new JobCommand(JobType.JOB_NEW.getType(), branchJob.getName(), branchJob.getPiperGroup(), branchJob.getSourceRedis(),
                        branchJob.getBackupPipers().stream().map(c->c.getLocation()).collect(Collectors.toList()),
                        branchJob.getConsumePipers().stream().map(c->c.getLocation()).collect(Collectors.toList())));
            }
        }
    }

    /**
     *
     * @param pipers
     * @param sourceRedis
     * @return
     */
    private NamePiper selectPiper(List<NamePiper> pipers, String sourceRedis) {
        return pipers.get(ThreadLocalRandom.current().nextInt(pipers.size()));
    }
}
