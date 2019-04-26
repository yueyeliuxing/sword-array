package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.data.storage.DataPartitionSystem;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.LocatedDataEntry;
import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryReq;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import com.zq.sword.array.zpiper.server.piper.job.*;
import com.zq.sword.array.zpiper.server.piper.job.command.JobCommand;
import com.zq.sword.array.zpiper.server.piper.job.command.JobType;
import com.zq.sword.array.zpiper.server.piper.protocol.BrokerMsgProcessor;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperServiceProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisPiper implements Piper{

    private Logger logger = LoggerFactory.getLogger(RedisPiper.class);

    protected NamePiper namePiper;

    private PartitionSystem partitionSystem;

    private JobSystem jobSystem;

    /**
     * Piper服务提供通信
     */
    private PiperServiceProtocol piperServiceProtocol;

    /**
     * 请求piperNamer的客户端
     */
    private PiperNameProtocol piperNameProtocol;


    public RedisPiper(PiperConfig config) {
        this.namePiper = config.namePiper();
        this.piperServiceProtocol = createPiperServiceProtocol(config.piperLocation());
        this.partitionSystem = DataPartitionSystem.get(config.dataStorePath());
        this.piperNameProtocol = createPiperNameProtocol(config);
        this.jobSystem = JobSystem.getInstance();

    }

    /**
     * 创建piper->namer的通信客户端
     * @param config
     * @return
     */
    private PiperNameProtocol createPiperNameProtocol(PiperConfig config) {
        PiperNameProtocol piperNameProtocol = new PiperNameProtocol(config.namerLocation());
        piperNameProtocol.addJobCommandListener(new JobCommandEventListener());
        return piperNameProtocol;
    }

    /**
     * 创建piper向外提供服务
     * @param piperLocation
     * @return
     */
    private PiperServiceProtocol createPiperServiceProtocol(String piperLocation){
        PiperServiceProtocol piperServiceProtocol = new PiperServiceProtocol(piperLocation);
        piperServiceProtocol.setBrokerMsgProcessor(new BrokerMsgProcessor() {
            @Override
            public List<DataEntry> obtainMessages(DataEntryReq req) {
                Partition partition = partitionSystem.getPartition(req.getPartGroup(), req.getPartName());
                if(partition == null){
                    logger.warn("查询的分片不存在, group:{} name:{}", req.getPartGroup(), req.getPartName());
                    return null;
                }
                return partition.orderGet(req.getOffset(), req.getReqSize());
            }

            @Override
            public void handleLocatedMessage(LocatedDataEntry locatedMessage) {
                Partition partition = partitionSystem.getOrNewPartition(locatedMessage.getPartGroup(), locatedMessage.getPartName());
                partition.append(locatedMessage.getEntry());
            }
        });
        return piperServiceProtocol;
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

    /**
     * 任务命令监听器
     */
    private class JobCommandEventListener implements HotspotEventListener<JobCommand> {

        private Logger logger = LoggerFactory.getLogger(JobCommandEventListener.class);

        @Override
        public void listen(HotspotEvent<JobCommand> dataEvent) {
            JobCommand jobCommand = dataEvent.getData();
            JobType jobType = JobType.toType(jobCommand.getType());
            if(jobType == null){
                return;
            }
            Job job = null;
            switch (jobType){
                case JOB_NEW:
                    jobSystem.createJob(new JobConfig(jobCommand,  namePiper, partitionSystem), new JobTaskMonitor());
                    break;
                case JOB_START:
                    jobSystem.startJob(jobCommand.getName());
                    break;
                case JOB_DESTROY:
                    jobSystem.destroyJob(jobCommand.getName());
                    break;
                case REPLICATE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartReplicateTask();
                    break;
                case WRITE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartWriteTask();
                    break;
                default:
                    break;
            }
            logger.info("获取PiperNamer命令:{}", jobCommand);
        }
    }

    /**
     * Job健康监控器
     */
    private class JobTaskMonitor implements TaskMonitor{

        @Override
        public void monitor(TaskHealth health) {
            piperNameProtocol.reportJobHealth(health);
        }
    }

}
