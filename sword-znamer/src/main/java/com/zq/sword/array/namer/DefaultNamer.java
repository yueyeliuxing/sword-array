package com.zq.sword.array.namer;

import com.zq.sword.array.namer.config.PiperConfig;
import com.zq.sword.array.namer.job.MetaJobSupervisor;
import com.zq.sword.array.namer.piper.MetaPiperSupervisor;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.network.rpc.protocol.processor.NamerServiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class DefaultNamer extends AbstractNamer implements Namer {

    private Logger logger = LoggerFactory.getLogger(DefaultNamer.class);

    /**
     * Piper管理器
     */
    private MetaPiperSupervisor metaPiperSupervisor;

    /**
     * Job管理器
     */
    private MetaJobSupervisor metaJobSupervisor;


    public DefaultNamer(PiperConfig config) {
        super(config.namerLocation());
        //创建piper管理器
        metaPiperSupervisor = new MetaPiperSupervisor();

        //创建Job管理器
        metaJobSupervisor = new MetaJobSupervisor(metaPiperSupervisor);

        //设置piper事件处理器
        setNamerServiceProcessor(new DefaultNamerServiceProcessor());
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {

    }

    /**
     * 处理器
     */
    private class DefaultNamerServiceProcessor extends NamerServiceProcessor {
        @Override
        public void handlePiperRegister(NamePiper namePiper) {
            metaPiperSupervisor.registerPiper(namePiper);
        }

        @Override
        public JobCommand handleJobCommandReq(NamePiper namePiper) {
            return metaJobSupervisor.getJobCommand(namePiper);
        }

        @Override
        public void handleTaskHealthReport(JobHealth jobHealth) {
            metaJobSupervisor.reportJobHealth(jobHealth);
        }

        @Override
        public void handleClientCreateJobReq(NameJob nameJob) {
            metaJobSupervisor.createJob(nameJob);
        }

        @Override
        public void handleClientStartJobReq(String jobName) {
            metaJobSupervisor.startJob(jobName);
        }

        @Override
        public void handleClientRemoveJobReq(String jobName) {
            metaJobSupervisor.removeJob(jobName);
        }
    }
}
