package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.zpiper.server.piper.job.monitor.JobHealth;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-24 16:31
 **/
public class Job extends AbstractTask implements Task {

    /**
     * 复制任务
     */
    private ReplicateTask replicateTask;

    /**
     * 写入任务
     */
    private WriteTask writeTask;

    public Job(String name) {
        super(name);
    }

    /**
     * 添加复制任务
     * @param replicateTask
     */
    public void setReplicateTask(ReplicateTask replicateTask){
        replicateTask.setTaskMonitor((taskHealth)->{
            taskMonitor.monitor(new JobHealth(name(), taskHealth.getState(), taskHealth, null));
        });
        this.replicateTask = replicateTask;
    }

    /**
     * 添加写入任务
     * @param writeTask
     */
    public void setWriteTask(WriteTask writeTask){
        writeTask.setTaskMonitor((taskHealth)->{
            taskMonitor.monitor(new JobHealth(name(), taskHealth.getState(), null, taskHealth));
        });
        this.writeTask = writeTask;
    }

    @Override
    public void run() {
        replicateTask.start();
        writeTask.start();
    }

    /**
     * 重启ReplicateTask
     */
    public void restartReplicateTask(){
        replicateTask.start();
    }

    /**
     * 重启WriteTask
     */
    public void restartWriteTask(){
        replicateTask.start();
    }

    @Override
    public void stop() {
        replicateTask.stop();
        writeTask.stop();
        super.stop();
    }
}
