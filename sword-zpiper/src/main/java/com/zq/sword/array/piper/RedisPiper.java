package com.zq.sword.array.piper;

import com.zq.sword.array.network.rpc.protocol.InterPiperProtocol;
import com.zq.sword.array.network.rpc.protocol.PiperNameProtocol;
import com.zq.sword.array.network.rpc.protocol.PiperServiceProtocol;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskHealth;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskMonitor;
import com.zq.sword.array.piper.config.PiperConfig;
import com.zq.sword.array.piper.job.JobController;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
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
public class RedisPiper implements Piper{

    private Logger logger = LoggerFactory.getLogger(RedisPiper.class);

    protected NamePiper namePiper;

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
        this.namePiper = config.namePiper();
        this.piperServiceProtocol = createPiperServiceProtocol(config.piperLocation());
        this.piperNameProtocol = createPiperNameProtocol(config);

        /**
         * Job控制器
         */
        this.jobController = new JobController(config.dataStorePath(), new JobTaskMonitor());

        //设置Job命令处理器
        this.piperNameProtocol.setJobCommandProcessor(jobController);

        //Job运行时存储处理器
        this.piperServiceProtocol.setJobRuntimeStorageProcessor(jobController);

        this.timedTaskExecutor = new SingleTimedTaskExecutor();
    }

    /**
     * 创建piper->namer的通信客户端
     * @param config
     * @return
     */
    private PiperNameProtocol createPiperNameProtocol(PiperConfig config) {
        PiperNameProtocol piperNameProtocol = new PiperNameProtocol(config.namerLocation());
        return piperNameProtocol;
    }

    /**
     * 创建piper向外提供服务
     * @param piperLocation
     * @return
     */
    private PiperServiceProtocol createPiperServiceProtocol(String piperLocation){
        PiperServiceProtocol piperServiceProtocol = new PiperServiceProtocol(piperLocation);
        return piperServiceProtocol;
    }

    /**
     * Job健康监控器
     */
    private class JobTaskMonitor implements TaskMonitor {

        @Override
        public void monitor(TaskHealth health) {
            health.setId(namePiper.getId());
            health.setGroup(namePiper.getGroup());
            health.setLocation(namePiper.getLocation());
            piperNameProtocol.reportJobHealth(health);
        }
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
}
