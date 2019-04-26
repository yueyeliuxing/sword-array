package com.zq.sword.array.zpiper.server.piper.cluster.protocol;

import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.network.rpc.server.NettyRpcServer;
import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.LocatedDataEntry;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.DataEntryReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: piper服务
 * @author: zhouqi1
 * @create: 2019-04-24 20:29
 **/
public class PiperServiceProtocol implements Actuator{

    /**
     * piper服务器 监听外来请求发送数据
     */
    protected NettyRpcServer rpcServer;

    /**
     * broker消息处理器
     */
    private DataPartitionProcessor brokerMsgProcessor;

    public PiperServiceProtocol(String piperLocation) {
        String[] params = piperLocation.split(":");
        this.rpcServer = new NettyRpcServer(Integer.parseInt(params[1]));
        this.rpcServer.registerTransferHandler(new PiperProtocolServiceTransferHandler());
    }

    @Override
    public void start() {
        rpcServer.start();
        while (!rpcServer.started()){
            try {
                Thread.sleep(110);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置Broker 消息处理器
     * @param brokerMsgProcessor
     */
    public void setDataPartitionProcessor(DataPartitionProcessor brokerMsgProcessor){
        this.brokerMsgProcessor = brokerMsgProcessor;
    }

    @Override
    public void stop() {
        rpcServer.shutdown();
    }

    /**
     * 数据传输
     */
    @ChannelHandler.Sharable
    private class PiperProtocolServiceTransferHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(PiperProtocolServiceTransferHandler.class);

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
            TransferMessage message = (TransferMessage)msg;
            logger.info("receive msg request : {}", message);
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_DATA_REQ.value()) {
                DataEntryReq msgReq = (DataEntryReq)message.getBody();
                List<DataEntry> msgs  = brokerMsgProcessor.obtainDataEntries(msgReq);
                ctx.writeAndFlush(buildReceiveMessageResp(msgs));
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_MESSAGE_REQ.value()) {
                LocatedDataEntry locatedMessage = (LocatedDataEntry)message.getBody();
                brokerMsgProcessor.handleLocatedEntry(locatedMessage);
                ctx.writeAndFlush(buildSendMessageResp(locatedMessage.getMessage()));
            }else {
                ctx.fireChannelRead(msg);
            }
        }

        private TransferMessage buildReceiveMessageResp(List<DataEntry> msgs) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.RECEIVE_DATA_RESP.value());
            message.setHeader(header);
            if(msgs != null){
                message.setBody(msgs);
            }
            return message;
        }

        private TransferMessage buildSendMessageResp(DataEntry msg) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_MESSAGE_RESP.value());
            message.setHeader(header);
            if(msg != null){
                message.setBody(msg.getSeq());
            }
            return message;
        }
    }
}
