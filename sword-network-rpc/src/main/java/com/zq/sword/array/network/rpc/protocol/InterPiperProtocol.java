package com.zq.sword.array.network.rpc.protocol;

import com.zq.sword.array.network.rpc.framework.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.client.RpcClient;
import com.zq.sword.array.network.rpc.framework.handler.TransferHandler;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.processor.BackupDataResultProcessor;
import com.zq.sword.array.network.rpc.protocol.processor.ConsumeDataResultProcessor;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 远程分片
 * @author: zhouqi1
 * @create: 2019-01-16 19:32
 **/
public class InterPiperProtocol implements Protocol {

    private Map<String, InterPiperClient> piperRpcClients;

    private InterPiperProtocol() {
        this.piperRpcClients = new HashMap<>();
    }

    public static InterPiperProtocol getInstance(){
        return InterPiperProtocolBuilder.INTER_PIPER_PROTOCOL;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        for(InterPiperClient piperClient : piperRpcClients.values()){
            piperClient.disconnect();
        }
    }

    private static class InterPiperProtocolBuilder {
        public static InterPiperProtocol INTER_PIPER_PROTOCOL = new InterPiperProtocol();
    }

    /**
     * 创建
     * @param piperLocation
     * @return
     */
    public InterPiperClient getOrNewInterPiperClient(String type,String jobName, String piperLocation){
        InterPiperClient interPiperClient = piperRpcClients.get(generateInterPiperClientId(type, jobName, piperLocation));
        if(interPiperClient == null){
            synchronized (piperRpcClients){
                if(interPiperClient == null){
                    interPiperClient = new InterPiperClient(type, jobName, piperLocation);
                }
            }
        }
        return interPiperClient;
    }

    /**
     * 生成InterPiperClientId
     * @param type
     * @param jobName
     * @param piperLocation
     * @return
     */
    private String generateInterPiperClientId(String type, String jobName, String piperLocation) {
        return String.format("%s|%s|%s", type, jobName, piperLocation);
    }

    /**
     * piper之间的客户端
     */
    public static class InterPiperClient {

        public static final String BACKUP_TYPE = "backup";
        public static final String CONSUME_TYPE = "consume";

        private String type;

        private String jobName;

        private String piperLocation;

        private RpcClient rpcClient;

        public InterPiperClient(String type, String jobName, String piperLocation) {
            this.type = type;
            this.jobName = jobName;
            this.piperLocation = piperLocation;
            String[] ps = piperLocation.split(":");
            rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
        }

        /**
         * 设置备份数据 返回处理器
         * @param backupDataResultProcessor
         */
        public void setBackupDataResultProcessor(BackupDataResultProcessor backupDataResultProcessor) {
            rpcClient.registerProtocolProcessor(backupDataResultProcessor);
        }

        /**
         * 设置消费数据返回处理器
         * @param consumeDataResultProcessor
         */
        public void setConsumeDataResultProcessor(ConsumeDataResultProcessor consumeDataResultProcessor) {
            rpcClient.registerProtocolProcessor(consumeDataResultProcessor);
        }

        public void connect() {
            rpcClient.start();
        }

        /**
         * 获取指定piper 指定分片 指定偏移量的消息
         * @param msgReq
         * @return
         */
        public void sendReplicateDataReq(ReplicateDataReq msgReq){
            rpcClient.write(buildReceiveMessageReq(msgReq));
        }

        /**
         * 发送复制数据
         * @param message
         */
        public void sendReplicateData(ReplicateData message){
            rpcClient.write(buildSendReplicateDataReq(message));
        }

        /**
         * 发送需要消费的下一个offset
         * @param consumeNextOffset
         */
        public void sendConsumeNextOffset(ConsumeNextOffset consumeNextOffset){
            rpcClient.write(buildSendConsumeNextOffsetReq(consumeNextOffset));
        }

        public void disconnect() {
            rpcClient.close();
            synchronized (InterPiperProtocol.getInstance().piperRpcClients){
                String interPiperClientId = InterPiperProtocol.getInstance().generateInterPiperClientId(type, jobName, piperLocation);
                if(InterPiperProtocol.getInstance().piperRpcClients.containsKey(interPiperClientId)){
                    InterPiperProtocol.getInstance().piperRpcClients.remove(interPiperClientId);
                }

            }

        }

        public boolean isClose() {
            return rpcClient.isClose();
        }

        /**
         * 构建接收消息请求
         * @param msgReq
         * @return
         */
        private TransferMessage buildReceiveMessageReq(ReplicateDataReq msgReq) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.RECEIVE_REPLICATE_DATA_REQ.value());
            message.setHeader(header);
            message.setBody(msgReq);
            return message;

        }

        /**
         * 构建发送数据请求
         * @param msg
         * @return
         */
        private TransferMessage buildSendReplicateDataReq(ReplicateData msg) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_REPLICATE_DATA_REQ.value());
            message.setHeader(header);
            message.setBody(msg);
            return message;
        }

        /**
         * 构建发送数据请求
         * @param consumeNextOffset
         * @return
         */
        private TransferMessage buildSendConsumeNextOffsetReq(ConsumeNextOffset consumeNextOffset) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_CONSUME_NEXT_OFFSET_REQ.value());
            message.setHeader(header);
            message.setBody(consumeNextOffset);
            return message;
        }
    }
}
