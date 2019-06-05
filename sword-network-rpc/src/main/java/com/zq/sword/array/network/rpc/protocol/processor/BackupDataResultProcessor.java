package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public class BackupDataResultProcessor implements ProtocolProcessor {

    private Logger logger = LoggerFactory.getLogger(BackupDataResultProcessor.class);

    /**
     * 接收到已备份的数据
     * @param replicateDataId
     * @return
     */
    public void handleBackupReplicateDataResult(ReplicateDataId replicateDataId){

    }

    /**
     * 接收到已经同步完成的消费偏移量
     * @param consumeNextOffset
     */
   public void handleBackupConsumeNextOffsetResult(ConsumeNextOffset consumeNextOffset){

   }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && (message.getHeader().getType() == MessageType.SEND_REPLICATE_DATA_RESP.value()
                || message.getHeader().getType() == MessageType.SEND_CONSUME_NEXT_OFFSET_RESP.value());
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_REPLICATE_DATA_RESP.value()) {
            ReplicateDataId replicateDataId = (ReplicateDataId)message.getBody();
            handleBackupReplicateDataResult(replicateDataId);
            logger.info("获取已经消费的消息ID:{}", replicateDataId);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_CONSUME_NEXT_OFFSET_RESP.value()) {
            ConsumeNextOffset consumeNextOffset = (ConsumeNextOffset)message.getBody();
            handleBackupConsumeNextOffsetResult(consumeNextOffset);
            logger.info("获取已经成功同步的消费offset信息:{}", consumeNextOffset);
        }
        return null;
    }
}
