package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.msg.LocatedMessage;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.msg.MsgReq;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.network.rpc.server.NettyRpcServer;
import com.zq.sword.array.network.rpc.server.RpcServer;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: broker
 * @author: zhouqi1
 * @create: 2019-01-17 21:09
 **/
public class ApplicationBroker extends AbstractApplicationBroker implements Broker{

    private RpcServer rpcServer;

    public ApplicationBroker(long id, String resourceLocation, NameCoordinator coordinator, String brokerLocation) {
        super(id, resourceLocation, coordinator, brokerLocation);
        this.rpcServer = createRpcServer(brokerLocation);
    }

    public ApplicationBroker(long id, String resourceLocation, NameCoordinator coordinator, String brokerLocation, RpcServer rpcServer) {
        super(id, resourceLocation, coordinator, brokerLocation);
        this.rpcServer = rpcServer;
    }

    /**
     * 创建Rpc 服务
     * @param brokerLocation
     * @return
     */
    private RpcServer createRpcServer(String brokerLocation) {
        String[] params = brokerLocation.split(":");
        NettyRpcServer nettyRpcServer = new NettyRpcServer(Integer.parseInt(params[1]));
        nettyRpcServer.registerTransferHandler(new MessageDataTransferHandler());
        return nettyRpcServer;
    }

    @Override
    public void start() {
        super.start();
        rpcServer.start();
    }

    @Override
    public void shutdown() {
        rpcServer.shutdown();
    }

    @Override
    public boolean started() {
        return rpcServer.started();
    }

    /**
     * 数据传输
     */
    private class MessageDataTransferHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(MessageDataTransferHandler.class);

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
            logger.error("receive msg request : {}", msg);
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_DATA_REQ.value()) {
                MsgReq msgReq = (MsgReq)message.getBody();
                Partition partition = getPartition(msgReq.getPartId());
                ObjectInputStream inputStream = partition.openInputStream();
                inputStream.skip(msgReq.getMsgId());
                Object[] objs = new Object[msgReq.getMsgSize()];
                inputStream.readObject(objs);
                inputStream.close();
                List<Message> msgs = new ArrayList<>();
                for(Object obj : objs){
                    if(obj != null){
                        msgs.add((Message)obj);
                    }
                }
                ctx.writeAndFlush(buildReceiveMessageResp(msgs));
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_MESSAGE_REQ.value()) {
                LocatedMessage locatedMessage = (LocatedMessage)message.getBody();
                Partition partition = getPartition(locatedMessage.getPartId());
                ObjectOutputStream inputStream = partition.openOutputStream();
                inputStream.writeObject(locatedMessage.getMessage());
                inputStream.close();
                ctx.writeAndFlush(buildSendMessageResp(locatedMessage.getMessage()));
            }else {
                ctx.fireChannelRead(msg);
            }
        }

        private TransferMessage buildReceiveMessageResp(List<Message> msgs) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_MESSAGE_RESP.value());
            message.setHeader(header);
            if(msgs != null){
                message.setBody(msgs);
            }
            return message;
        }

        private TransferMessage buildSendMessageResp(Message msg) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_MESSAGE_RESP.value());
            message.setHeader(header);
            if(msg != null){
                message.setBody(msg.getMsgId());
            }
            return message;
        }
    }
}
