package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.data.storage.DataPartitionSystem;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import com.zq.sword.array.zpiper.server.piper.job.JobControlCluster;
import com.zq.sword.array.zpiper.server.piper.protocol.BrokerMsgProcessor;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperServiceProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryReq;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.LocatedDataEntry;
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
    private JobControlCluster jobEnvCluster;


    public RedisPiper(PiperConfig config) {
        this.namePiper = config.namePiper();
        this.piperServiceProtocol = createPiperServiceProtocol(config.piperLocation());
        this.partitionSystem = DataPartitionSystem.get(config.dataStorePath());
        this.piperNameProtocol = createPiperNameProtocol(config);
        this.jobEnvCluster = new JobControlCluster(piperNameProtocol, partitionSystem);
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
}
