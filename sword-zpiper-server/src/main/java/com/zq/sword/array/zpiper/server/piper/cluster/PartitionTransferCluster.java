package com.zq.sword.array.zpiper.server.piper.cluster;

import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.DataPartitionProcessor;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.PiperServiceProtocol;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.DataEntryReq;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.LocatedDataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 分片传输集群控制
 * @author: zhouqi1
 * @create: 2019-04-26 15:56
 **/
public class PartitionTransferCluster {

    private Logger logger = LoggerFactory.getLogger(PartitionTransferCluster.class);

    private PiperServiceProtocol piperServiceProtocol;

    private PartitionSystem partitionSystem;

    public PartitionTransferCluster(PiperServiceProtocol piperServiceProtocol, PartitionSystem partitionSystem) {
        this.partitionSystem = partitionSystem;
        this.piperServiceProtocol = piperServiceProtocol;
        this.piperServiceProtocol.setDataPartitionProcessor(new DataPartitionProcessor() {
            @Override
            public List<DataEntry> obtainDataEntries(DataEntryReq req) {
                Partition partition = PartitionTransferCluster.this.partitionSystem.getPartition(req.getPartGroup(), req.getPartName());
                if(partition == null){
                    logger.warn("查询的分片不存在, group:{} name:{}", req.getPartGroup(), req.getPartName());
                    return null;
                }
                return partition.orderGet(req.getOffset(), req.getReqSize());
            }

            @Override
            public void handleLocatedEntry(LocatedDataEntry locatedEntry) {
                Partition partition = PartitionTransferCluster.this.partitionSystem.getOrNewPartition(locatedEntry.getPartGroup(), locatedEntry.getPartName());
                partition.append(locatedEntry.getEntry());
            }
        });
    }

}
