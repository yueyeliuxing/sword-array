package com.zq.sword.array.piper;

import com.zq.sword.array.network.rpc.protocol.InterPiperProtocol;
import com.zq.sword.array.network.rpc.protocol.PiperNameProtocol;
import com.zq.sword.array.network.rpc.protocol.PiperServiceProtocol;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.piper.job.JobMonitor;
import com.zq.sword.array.network.rpc.protocol.processor.PiperNameProcessor;
import com.zq.sword.array.network.rpc.protocol.processor.PiperServiceProcessor;
import com.zq.sword.array.piper.config.PiperConfig;
import com.zq.sword.array.piper.job.JobController;
import com.zq.sword.array.piper.storage.RedisDataStorage;
import com.zq.sword.array.piper.storage.LocalRedisDataStorage;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisPiper implements Piper{

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
     * Piper服务提供通信
     */
    private PiperServiceProtocol piperServiceProtocol;

    /**
     * 请求piperNamer的客户端
     */
    private PiperNameProtocol piperNameProtocol;

    /**
     * 分布式任务执行器
     */
    private JobController jobController;

    /**
     * 定时任务执行器
     */
    private TimedTaskExecutor timedTaskExecutor;


    public RedisPiper(PiperConfig config) {
        namePiper = config.namePiper();

        /**
         * 分片存储系统
         */
        redisDataStorage =  new LocalRedisDataStorage(config.dataStorePath());

        /**
         * Piper服务 接收其他piper或者namer的数据
         */
        piperServiceProtocol = new PiperServiceProtocol(config.piperLocation());
        piperServiceProtocol.setPiperServiceProcessor(new DefaultPiperServiceProcessor());

        /**
         *  连接Namer的客户端  发送数据 接收命令
         */
        piperNameProtocol = new PiperNameProtocol(config.namerLocation());
        piperNameProtocol.setPiperNameProcessor(new DefaultPiperNameProcessor());

        /**
         * Job控制器
         */
        jobController = new JobController(redisDataStorage);
        jobController.setJobMonitor(new DefaultJobMonitor());

        timedTaskExecutor = new SingleTimedTaskExecutor();
    }

    @Override
    public void start() {
        piperServiceProtocol.start();
        piperNameProtocol.start();

        //向namer注册piper
        piperNameProtocol.registerPiper(namePiper);

        //定时进行Job命令请求
        timedTaskExecutor.timedExecute(()->{
            piperNameProtocol.reqJobCommand(namePiper);
        },500, TimeUnit.MILLISECONDS);

    }

    @Override
    public void stop() {
        piperNameProtocol.stop();
        piperServiceProtocol.stop();
        InterPiperProtocol.getInstance().stop();
    }

    /**
     * PiperName处理器
     */
    private class DefaultPiperNameProcessor implements PiperNameProcessor {

        @Override
        public void acceptJobCommand(JobCommand command) {
            jobController.handleJobCommand(command);
        }
    }

    /**
     * PipserService 处理器
     */
    private class DefaultPiperServiceProcessor implements PiperServiceProcessor {

        @Override
        public List<ReplicateData> handleReplicateDataReq(ReplicateDataReq req) {
            return redisDataStorage.readReplicateData(req);
        }

        @Override
        public void handleReplicateData(ReplicateData replicateData) {
            redisDataStorage.writeReplicateData(replicateData);
        }

        @Override
        public void handleConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
            redisDataStorage.writeConsumedNextOffset(consumeNextOffset);
        }
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
            piperNameProtocol.reportJobHealth(health);
        }
    }
}
