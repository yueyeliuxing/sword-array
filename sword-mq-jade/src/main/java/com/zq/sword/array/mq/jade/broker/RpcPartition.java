package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.LocatedMessage;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.msg.MsgReq;
import com.zq.sword.array.network.rpc.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.client.RpcClient;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.stream.io.AbstractResourceInputStream;
import com.zq.sword.array.stream.io.AbstractResourceOutputStream;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 远程分片
 * @author: zhouqi1
 * @create: 2019-01-16 19:32
 **/
public class RpcPartition implements Partition {

    private long id;

    //ip:port
    private String location;

    private String topic;

    /**
     * 发送消息队列
     */
    private Queue<Object> sendMsgQueue;

    /**
     * 接收消息队列
     */
    private BlockingQueue<Object> receiveMsgQueue;

    private RpcClient client;

    public RpcPartition(long id, String location, String topic) {
        this.id = id;
        this.location = location;
        this.topic = topic;
        String[] ps = location.split(":");
        this.sendMsgQueue = new LinkedBlockingQueue<>();
        this.receiveMsgQueue = new LinkedBlockingQueue<>();
        client = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
        client.registerTransferHandler(new SendMsgToRemoteHostHandler(sendMsgQueue, receiveMsgQueue));
        client.connect();
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String name() {
        return location;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public ObjectInputStream openInputStream() throws InputStreamOpenException {
        return new RpcPartitionInputStream(sendMsgQueue, receiveMsgQueue);
    }

    @Override
    public ObjectOutputStream openOutputStream() throws OutputStreamOpenException {
        return new RpcPartitionOutputStream(sendMsgQueue);
    }

    @Override
    public void close() {
        client.disconnect();
    }

    /**
     * 读取数据
     */
    private class RpcPartitionInputStream extends AbstractResourceInputStream implements ObjectInputStream {

        private Logger logger = LoggerFactory.getLogger(RpcPartitionInputStream.class);

        private Queue<Object> sendMsgQueue;

        private BlockingQueue<Object> receiveMsgQueue;

        private long msgId;

        public RpcPartitionInputStream(Queue<Object> sendMsgQueue, BlockingQueue<Object> receiveMsgQueue) {
            this.sendMsgQueue = sendMsgQueue;
            this.receiveMsgQueue = receiveMsgQueue;
        }

        @Override
        public void skip(long msgId) throws IOException {
            this.msgId = msgId;
        }

        @Override
        public Object readObject() throws IOException {
            sendMsgQueue.offer(new MsgReq(id, msgId, 1));
            Object obj =  receiveMsgQueue.poll();
            if(obj instanceof Message){
                return obj;
            }
            return null;
        }

        @Override
        public void readObject(Object[] objs) throws IOException {
            sendMsgQueue.offer(new MsgReq(id, msgId, objs.length));

        }

        @Override
        public void close() throws IOException {

        }
    }

    /**
     * 输出流
     */
    private class RpcPartitionOutputStream extends AbstractResourceOutputStream implements ObjectOutputStream {

        private Queue<Object> sendMsgQueue;

        public RpcPartitionOutputStream(Queue<Object> sendMsgQueue) {
            this.sendMsgQueue = sendMsgQueue;
        }

        @Override
        public void writeObject(Object obj) throws IOException {
            sendMsgQueue.offer(obj);
        }

        @Override
        public void writeObject(List<Object> objs) throws IOException {
            sendMsgQueue.addAll(objs);
        }

        @Override
        public void close() throws IOException {

        }
    }


    /**
     * 发送数据到远程broker
     */
    private class SendMsgToRemoteHostHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(SendMsgToRemoteHostHandler.class);

        private volatile ScheduledFuture<?> sendMsgFuture;

        private Queue<Object> sendMsgQueue;

        private Queue<Object> receiveMsgQueue;

        public SendMsgToRemoteHostHandler(Queue<Object> sendMsgQueue,  Queue<Object> receiveMsgQueue) {
            this.sendMsgQueue = sendMsgQueue;
            this.receiveMsgQueue = receiveMsgQueue;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if(sendMsgFuture != null) {
                sendMsgFuture.cancel(true);
                sendMsgFuture = null;
            }
            ctx.fireExceptionCaught(cause);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if(sendMsgFuture == null) {
                sendMsgFuture = ctx.executor().scheduleAtFixedRate(new SendMsgTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            }

            TransferMessage message = (TransferMessage)msg;
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_MESSAGE_RESP.value()) {
                Long msgId = (Long)message.getBody();
                receiveMsgQueue.offer(msgId);
                logger.info("获取已经消费的消息ID:{}", msgId);
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.RECEIVE_DATA_RESP.value()) {
                List<Message> messages = (List<Message>)message.getBody();
                receiveMsgQueue.addAll(messages);
                logger.info("获取要查询的数据:{}", messages);
            }else {
                ctx.fireChannelRead(msg);
            }
        }

        public class SendMsgTask implements Runnable {

            private final ChannelHandlerContext ctx;

            public SendMsgTask(ChannelHandlerContext ctx) {
                this.ctx = ctx;
            }

            @Override
            public void run() {
                Object obj = sendMsgQueue.poll();
                if(obj != null){
                    TransferMessage transferMessage = null;
                    if(obj instanceof Message){
                        transferMessage = buildSendMessageReq(new LocatedMessage(id, (Message)obj));
                    }else if(obj instanceof MsgReq){
                        transferMessage = buildReceiveMessageReq((MsgReq) obj);
                    }else {
                        return;
                    }
                    System.out.println("Client send message to server : --> " + transferMessage);
                    ctx.writeAndFlush(transferMessage);
                }
            }

            /**
             * 构建接收消息请求
             * @param msgReq
             * @return
             */
            private TransferMessage buildReceiveMessageReq(MsgReq msgReq) {
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
            private TransferMessage buildSendMessageReq(LocatedMessage msg) {
                TransferMessage message = new TransferMessage();
                Header header = new Header();
                header.setType(MessageType.SEND_MESSAGE_REQ.value());
                message.setHeader(header);
                message.setBody(msg);
                return message;
            }
        }
    }
}
