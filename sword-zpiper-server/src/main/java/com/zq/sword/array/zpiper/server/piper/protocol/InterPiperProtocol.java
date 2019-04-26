package com.zq.sword.array.zpiper.server.piper.protocol;

import com.zq.sword.array.common.event.DefaultHotspotEventEmitter;
import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventEmitter;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.network.rpc.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.client.RpcClient;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.LocatedDataEntry;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryReq;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryResp;
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
public class InterPiperProtocol {

    private Map<String, InterPiperClient> piperRpcClients;

    private InterPiperProtocol() {
        this.piperRpcClients = new HashMap<>();
    }

    public static InterPiperProtocol getInstance(){
        return InterPiperProtocolBuilder.INTER_PIPER_PROTOCOL;
    }

    private static class InterPiperProtocolBuilder {
        public static InterPiperProtocol INTER_PIPER_PROTOCOL = new InterPiperProtocol();
    }

    /**
     * 创建
     * @param piperLocation
     * @return
     */
    public InterPiperClient getOrNewInterPiperClient(String piperLocation){
        InterPiperClient interPiperClient = piperRpcClients.get(piperLocation);
        if(interPiperClient == null){
            synchronized (piperRpcClients){
                if(interPiperClient == null){
                    interPiperClient = new InterPiperClient(piperLocation);
                    interPiperClient.connect();
                }
            }
        }
        return interPiperClient;
    }
    /**
     * piper之间的客户端
     */
    public static class InterPiperClient {

        private RpcClient rpcClient;

        private HotspotEventEmitter<List<LocatedDataEntry>> messageObtainEventEmitter;

        private HotspotEventEmitter<DataEntryResp> messageWriteRespEventEmitter;

        public InterPiperClient(String location) {
            messageObtainEventEmitter = new DefaultHotspotEventEmitter();
            messageWriteRespEventEmitter = new DefaultHotspotEventEmitter();
            String[] ps = location.split(":");
            rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
            rpcClient.registerTransferHandler(new InterPiperTransferHandler());
        }

        /**
         * 添加消息获取监听器
         * @param eventListener
         */
        public void addMessageObtainEventListener(HotspotEventListener<List<LocatedDataEntry>> eventListener){
            messageObtainEventEmitter.registerEventListener(eventListener);
        }

        /**
         * 添加消息获取监听器
         * @param eventListener
         */
        public void addMessageWriteRespEventListener(HotspotEventListener<DataEntryResp> eventListener){
            messageWriteRespEventEmitter.registerEventListener(eventListener);
        }

        public void connect() {
            rpcClient.connect();
        }

        /**
         * 获取指定piper 指定分片 指定偏移量的消息
         * @param msgReq
         * @return
         */
        public void sendMessageReq(DataEntryReq msgReq){
            rpcClient.write(buildReceiveMessageReq(msgReq));
        }

        public void sendMessage(LocatedDataEntry message){
            rpcClient.write(buildSendMessageReq(message));
        }

        public void disconnect() {
            rpcClient.disconnect();
        }

        public boolean isClose() {
            return rpcClient.isClose();
        }

        /**
         * 构建接收消息请求
         * @param msgReq
         * @return
         */
        private TransferMessage buildReceiveMessageReq(DataEntryReq msgReq) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.RECEIVE_DATA_REQ.value());
            message.setHeader(header);
            message.setBody(msgReq);
            return message;

        }

        /**
         * 构建发送数据请求
         * @param msg
         * @return
         */
        private TransferMessage buildSendMessageReq(LocatedDataEntry msg) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_MESSAGE_REQ.value());
            message.setHeader(header);
            message.setBody(msg);
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
                if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_MESSAGE_RESP.value()) {
                    DataEntryResp msgResp = (DataEntryResp)message.getBody();
                    messageWriteRespEventEmitter.emitter(new HotspotEvent(msgResp));
                    logger.info("获取已经消费的消息ID:{}", msgResp);
                }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_DATA_RESP.value()) {
                    List<LocatedDataEntry> messages = (List<LocatedDataEntry>)message.getBody();
                    messageObtainEventEmitter.emitter(new HotspotEvent(messages));
                    logger.info("获取要查询的数据:{}", messages);
                }else {
                    ctx.fireChannelRead(msg);
                }
            }
        }
    }
}
