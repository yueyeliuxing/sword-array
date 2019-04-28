package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import com.zq.sword.array.zpiper.server.piper.job.JobController;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskHealth;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskMonitor;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperServiceProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            piperNameProtocol.reportJobHealth(health);
        }
    }

    @Override
    public void start() {
        piperServiceProtocol.start();
        piperNameProtocol.start();

        //向namer注册piper
        piperNameProtocol.registerPiper(namePiper);

    }

    @Override
    public void stop() {
        piperNameProtocol.stop();
        piperServiceProtocol.stop();
    }
}
