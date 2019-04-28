package com.zq.sword.array.piper.protocol;

import com.zq.sword.array.network.rpc.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.client.RpcClient;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.piper.protocol.processor.BackupDataRespProcessor;
import com.zq.sword.array.piper.protocol.processor.ConsumeDataRespProcessor;
import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.piper.job.dto.ReplicateData;
import com.zq.sword.array.piper.job.dto.ReplicateDataId;
import com.zq.sword.array.piper.job.dto.ReplicateDataReq;
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
public class InterPiperProtocol implements Actuator {

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
                    interPiperClient.connect();
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

        private volatile BackupDataRespProcessor backupDataRespProcessor;

        private volatile ConsumeDataRespProcessor consumeDataRespProcessor;

        public InterPiperClient(String type, String jobName, String piperLocation) {
            this.type = type;
            this.jobName = jobName;
            this.piperLocation = piperLocation;
            String[] ps = piperLocation.split(":");
            rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
            rpcClient.registerTransferHandler(new InterPiperTransferHandler());
        }

        /**
         * 设置备份数据 返回处理器
         * @param backupDataRespProcessor
         */
        public void setBackupDataRespProcessor(BackupDataRespProcessor backupDataRespProcessor) {
            this.backupDataRespProcessor = backupDataRespProcessor;
        }

        /**
         * 设置消费数据返回处理器
         * @param consumeDataRespProcessor
         */
        public void setConsumeDataRespProcessor(ConsumeDataRespProcessor consumeDataRespProcessor) {
            this.consumeDataRespProcessor = consumeDataRespProcessor;
        }

        public void connect() {
            rpcClient.connect();
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
            rpcClient.disconnect();
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

        /**
         * 发送数据到远程broker
         */
        @ChannelHandler.Sharable
        private class InterPiperTransferHandler extends TransferHandler {

            private Logger logger = LoggerFactory.getLogger(InterPiperTransferHandler.class);

            public InterPiperTransferHandler() {
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.fireExceptionCaught(cause);
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                logger.info("rpcPartition receive msg request : {}", msg);
                TransferMessage message = (TransferMessage)msg;
                if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_REPLICATE_DATA_RESP.value()) {
                    ReplicateDataId replicateDataId = (ReplicateDataId)message.getBody();
                    if(backupDataRespProcessor != null){
                        backupDataRespProcessor.backupReplicateData(replicateDataId);
                    }
                    logger.info("获取已经消费的消息ID:{}", replicateDataId);
                }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_REPLICATE_DATA_RESP.value()) {
                    List<ReplicateData> replicateData = (List<ReplicateData>)message.getBody();
                    if(consumeDataRespProcessor != null){
                        consumeDataRespProcessor.consumeReplicateData(replicateData);
                    }
                    logger.info("获取要查询的数据:{}", replicateData);
                }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_CONSUME_NEXT_OFFSET_RESP.value()) {
                    ConsumeNextOffset consumeNextOffset = (ConsumeNextOffset)message.getBody();
                    if(backupDataRespProcessor != null){
                        backupDataRespProcessor.backupConsumeNextOffset(consumeNextOffset);
                    }
                    logger.info("获取已经成功同步的消费offset信息:{}", consumeNextOffset);
                }else {
                    ctx.fireChannelRead(msg);
                }
            }
        }
    }
}
