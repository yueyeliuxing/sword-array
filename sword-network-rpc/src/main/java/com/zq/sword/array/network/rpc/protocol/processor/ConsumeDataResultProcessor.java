package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public class ConsumeDataResultProcessor implements ProtocolProcessor {
    private Logger logger = LoggerFactory.getLogger(ConsumeDataResultProcessor.class);
    /**
     * 接收到请求的数据
     * @param replicateData
     */
    public void handleReplicateData(List<ReplicateData> replicateData){

    }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && message.getHeader().getType() == MessageType.RECEIVE_REPLICATE_DATA_RESP.value();
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_REPLICATE_DATA_RESP.value()) {
            List<ReplicateData> replicateData = (List<ReplicateData>)message.getBody();
            handleReplicateData(replicateData);
            logger.info("获取要查询的数据:{}", replicateData);
        }
        return null;
    }
}
