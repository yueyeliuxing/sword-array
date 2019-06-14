package com.zq.sword.array.piper;

import com.zq.sword.array.piper.job.JobMonitor;
import com.zq.sword.array.piper.config.PiperConfig;
import com.zq.sword.array.piper.job.JobController;
import com.zq.sword.array.piper.service.RedisReplicateDataService;
import com.zq.sword.array.piper.storage.RedisDataStorage;
import com.zq.sword.array.piper.storage.LocalRedisDataStorage;
import com.zq.sword.array.rpc.api.namer.JobHandleService;
import com.zq.sword.array.rpc.api.namer.PiperHandleService;
import com.zq.sword.array.rpc.api.namer.dto.JobCommand;
import com.zq.sword.array.rpc.api.namer.dto.JobHealth;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;
import com.zq.sword.array.tasks.TaskExecutorPool;
import com.zq.sword.array.tasks.TimedTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisPiper extends AbstractPiper implements Piper{

    private Logger logger = LoggerFactory.getLogger(RedisPiper.class);

    /**
     * Piper
     */
    private NamePiper namePiper;

    /**
     * 任务数据存储
     */
    private RedisDataStorage redisDataStorage;

    /**
     * piper处理服务
     */
    private PiperHandleService piperHandleService;

    /**
     * 任务处理服务
     */
    private JobHandleService jobHandleService;

    /**
     * 分布式任务执行器
     */
    private JobController jobController;

    /**
     * 定时任务执行器
     */
    private TimedTaskExecutor timedTaskExecutor;


    public RedisPiper(PiperConfig config) {
        super(config);
        namePiper = config.namePiper();

        /**
         * 注册服务处理器
         */
        registerService(new RedisReplicateDataService(redisDataStorage));

        /**
         * 得到远程PiperHandleService
         */
        piperHandleService = getService(PiperHandleService.class);

        /**
         * 得到远程JobHandleService
         */
        jobHandleService = getService(JobHandleService.class);

        /**
         * 分片存储系统
         */
        redisDataStorage =  new LocalRedisDataStorage(config.dataStorePath());

        /**
         * Job控制器
         */
        jobController = new JobController(redisDataStorage);
        jobController.setJobMonitor(new DefaultJobMonitor());

        timedTaskExecutor = TaskExecutorPool.getCommonTimedTaskExecutor();
    }

    @Override
    public void start() {
        super.start();

        //向namer注册piper
        piperHandleService.registerPiper(namePiper);

        //定时进行Job命令请求
        timedTaskExecutor.timedExecute(()->{
            JobCommand jobCommand = piperHandleService.requestJobCommand(namePiper);
            jobController.handleJobCommand(jobCommand);
        },500, TimeUnit.MILLISECONDS);

    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    /**
     * Job健康监控器
     */
    private class DefaultJobMonitor implements JobMonitor {

        @Override
        public void monitor(JobHealth health) {
            health.setId(namePiper.getId());
            health.setGroup(namePiper.getGroup());
            health.setLocation(namePiper.getLocation());
            jobHandleService.reportJobHealth(health);
        }
    }
}
