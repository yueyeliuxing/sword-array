package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public class PiperServiceProcessor implements ProtocolProcessor {

    /**
     * 获取指定消息
     * @param req
     * @return
     */
    public List<ReplicateData> handleReplicateDataReq(ReplicateDataReq req){
        return null;
    }

    /**
     * 处理指定消息
     * @param replicateData
     */
    public void handleReplicateData(ReplicateData replicateData){

    }

    /**
     * 写入要消费下一个的offset
     * @param consumeNextOffset
     */
   public void handleConsumeNextOffset(ConsumeNextOffset consumeNextOffset){

   }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && (message.getHeader().getType() == MessageType.RECEIVE_REPLICATE_DATA_REQ.value()
                || message.getHeader().getType() == MessageType.SEND_REPLICATE_DATA_REQ.value()
                || message.getHeader().getType() == MessageType.SEND_CONSUME_NEXT_OFFSET_REQ.value());
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        TransferMessage transferMessage = null;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_REPLICATE_DATA_REQ.value()) {
            ReplicateDataReq msgReq = (ReplicateDataReq)message.getBody();
            List<ReplicateData> msgs  = handleReplicateDataReq(msgReq);
            transferMessage = buildReceiveMessageResp(msgs);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_REPLICATE_DATA_REQ.value()) {
            ReplicateData replicateData = (ReplicateData)message.getBody();
            handleReplicateData(replicateData);
            transferMessage = buildSendReplicateDataResp(new ReplicateDataId(replicateData.getPiperGroup(),
                    replicateData.getPiperGroup(), replicateData.getOffset()));
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_CONSUME_NEXT_OFFSET_REQ.value()) {
            ConsumeNextOffset consumeNextOffset = (ConsumeNextOffset)message.getBody();
            handleConsumeNextOffset(consumeNextOffset);
            transferMessage = buildSendConsumeNextOffsetResp(consumeNextOffset);
        }
        return transferMessage;
    }

    private TransferMessage buildReceiveMessageResp(List<ReplicateData> msgs) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.RECEIVE_REPLICATE_DATA_RESP.value());
        message.setHeader(header);
        if(msgs != null){
            message.setBody(msgs);
        }
        return message;
    }

    private TransferMessage buildSendReplicateDataResp(ReplicateDataId replicateDataId) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.SEND_REPLICATE_DATA_RESP.value());
        message.setHeader(header);
        message.setBody(replicateDataId);
        return message;
    }

    private TransferMessage buildSendConsumeNextOffsetResp(ConsumeNextOffset consumeNextOffset) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.SEND_CONSUME_NEXT_OFFSET_RESP.value());
        message.setHeader(header);
        message.setBody(consumeNextOffset);
        return message;
    }
}
