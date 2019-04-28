package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.handler.SimpleCycleDisposeHandler;
import com.zq.sword.array.zpiper.server.piper.job.storage.ClusterJobRuntimeStorage;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-24 16:31
 **/
public class Job {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务数据备份处理器
     */
    private ClusterJobRuntimeStorage clusterJobRuntimeStorage;

    /**
     * 复制任务
     */
    private ReplicateTask replicateTask;

    /**
     * 写入任务
     */
    private WriteTask writeTask;

    public Job(JobContext jobContext) {
        name = jobContext.getName();

        //创建Job运行时数据备份存储
        clusterJobRuntimeStorage = new ClusterJobRuntimeStorage(jobContext.getName(), jobContext.getBackupPipers(), jobContext.getJobRuntimeStorage());
        jobContext.setJobRuntimeStorage(clusterJobRuntimeStorage);

        CycleDisposeHandler<RedisCommand> cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //创建ReplicateTask
        replicateTask = new RedisReplicateTask(this, jobContext, cycleDisposeHandler);

        //创建WriteTask
        writeTask = new RedisWriteTask(this, jobContext, cycleDisposeHandler);
    }

    /**
     * 刷新任务备份piper
     * @param incrementBackupPipers
     * @param decreaseBackupPipers
     */
    public void flushJobBackupPipers(List<String> incrementBackupPipers, List<String> decreaseBackupPipers){
        clusterJobRuntimeStorage.flushJobBackupPipers(incrementBackupPipers, decreaseBackupPipers);
    }

    /**
     * 刷新任务消费piper
     * @param incrementConsumePipers
     * @param decreaseConsumePipers
     */
    public void flushJobConsumePipers(List<String> incrementConsumePipers, List<String> decreaseConsumePipers){
        writeTask.flushJobConsumePipers(incrementConsumePipers, decreaseConsumePipers);
    }

    /**
     * 获取任务名称
     * @return
     */
    public String name(){
        return name;
    }

    /**
     * 任务启动
     */
    public void start() {
        replicateTask.start();
        writeTask.start();
    }

    /**
     * 重启ReplicateTask
     */
    public void restartReplicateTask(){
        replicateTask.stop();
        replicateTask.start();
    }

    /**
     * 重启WriteTask
     */
    public void restartWriteTask(){
        writeTask.stop();
        writeTask.start();
    }

    /**
     * 任务销毁
     */
    public void destroy() {
        replicateTask.stop();
        writeTask.stop();
        clusterJobRuntimeStorage.destroy();
    }
}
