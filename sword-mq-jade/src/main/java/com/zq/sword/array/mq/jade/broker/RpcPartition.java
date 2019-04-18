package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.LocatedMessage;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.msg.MsgReq;
import com.zq.sword.array.mq.jade.msg.MsgResp;
import com.zq.sword.array.network.rpc.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.client.RpcClient;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import com.zq.sword.array.network.rpc.message.Header;
import com.zq.sword.array.network.rpc.message.MessageType;
import com.zq.sword.array.network.rpc.message.TransferMessage;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

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

    private String tag;

    /**
     * 发送消息队列
     */
    private Queue<Object> sendMsgQueue;

    /**
     * 发送消息请求对列
     */
    private SynchronousQueue<MsgReq> sendMsgReqQueue;

    /**
     * 接收消息队列
     */
    private BlockingQueue<Object> receiveMsgQueue;

    private RpcClient client;

    private TaskExecutor taskExecutor;

    public RpcPartition(long id, String location, String topic, String tag) {
        this.id = id;
        this.location = location;
        this.topic = topic;
        this.tag = tag;
        String[] ps = location.split(":");
        this.taskExecutor = new SingleTaskExecutor();
        this.sendMsgQueue = new LinkedBlockingQueue<>();
        this.sendMsgReqQueue = new SynchronousQueue<>();
        this.receiveMsgQueue = new LinkedBlockingQueue<>();
        client = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
        client.registerTransferHandler(new SendMsgToRemoteHostHandler(sendMsgQueue, sendMsgReqQueue, receiveMsgQueue));
        this.taskExecutor.execute(()->{
            client.connect();
        });
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String tag() {
        return tag;
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
    public long append(Message message) {
        sendMsgQueue.offer(message);
        //返回偏移量
        return 0;
    }

    @Override
    public Message search(long offset) {
        try{
            sendMsgReqQueue.put(new MsgReq(id, offset, 1));
            Object obj = receiveMsgQueue.poll(1, TimeUnit.SECONDS);
            if(obj != null && obj instanceof Message){
                return (Message) obj;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> orderSearch(long offset, int num) {
        List<Message> messages = new ArrayList<>();
        try {
            sendMsgReqQueue.put(new MsgReq(id, offset, num));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true){
            Object obj = null;
            try {
                obj = receiveMsgQueue.poll(1, TimeUnit.SECONDS);
                if(obj != null && obj instanceof Message){
                    messages.add((Message)obj);
                }else {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    @Override
    public boolean isClose() {
        return client.isClose();
    }

    @Override
    public void close() {
        client.disconnect();
    }

    /**
     * 发送数据到远程broker
     */
    @ChannelHandler.Sharable
    private class SendMsgToRemoteHostHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(SendMsgToRemoteHostHandler.class);

        private volatile ScheduledFuture<?> sendMsgFuture;

        private volatile ScheduledFuture<?> sendMsgReqFuture;

        private Queue<Object> sendMsgQueue;

        private SynchronousQueue<MsgReq> sendMsgReqQueue;

        private BlockingQueue<Object> receiveMsgQueue;

        public SendMsgToRemoteHostHandler(Queue<Object> sendMsgQueue, SynchronousQueue<MsgReq> sendMsgReqQueue,  BlockingQueue<Object> receiveMsgQueue) {
            this.sendMsgQueue = sendMsgQueue;
            this.sendMsgReqQueue = sendMsgReqQueue;
            this.receiveMsgQueue = receiveMsgQueue;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if(sendMsgFuture != null) {
                sendMsgFuture.cancel(true);
                sendMsgFuture = null;
            }
            if(sendMsgReqFuture != null) {
                sendMsgReqFuture.cancel(true);
                sendMsgReqFuture = null;
            }
            ctx.fireExceptionCaught(cause);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("rpcPartition receive msg request : {}", msg);
            if(sendMsgFuture == null) {
                sendMsgFuture = ctx.executor().scheduleAtFixedRate(new SendMsgTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            }

            if(sendMsgReqFuture == null) {
                sendMsgReqFuture = ctx.executor().scheduleAtFixedRate(new SendMsgReqTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            }

            TransferMessage message = (TransferMessage)msg;
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.SEND_MESSAGE_RESP.value()) {
                MsgResp msgResp = (MsgResp)message.getBody();
                receiveMsgQueue.offer(msgResp);
                logger.info("获取已经消费的消息ID:{}", msgResp);
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
                logger.info("定时获取消息数据，request->{} queue: {}", obj, sendMsgQueue);
                if(obj != null){
                    TransferMessage transferMessage = buildSendMessageReq(new LocatedMessage(id, (Message)obj));
                    System.out.println("Client send message to server : --> " + transferMessage);
                    ctx.writeAndFlush(transferMessage);
                }else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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

        public class SendMsgReqTask implements Runnable {

            private final ChannelHandlerContext ctx;

            public SendMsgReqTask(ChannelHandlerContext ctx) {
                this.ctx = ctx;
            }

            @Override
            public void run() {
                MsgReq msgReq = sendMsgReqQueue.poll();
                logger.info("定时获取消息数据，request->{} queue: {}", msgReq, sendMsgQueue);
                if(msgReq != null){
                    TransferMessage transferMessage = buildReceiveMessageReq(msgReq);
                    System.out.println("Client send message req to server : --> " + transferMessage);
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
        }
    }
}
