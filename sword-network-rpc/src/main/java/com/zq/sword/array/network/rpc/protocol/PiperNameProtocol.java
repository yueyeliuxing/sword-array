package com.zq.sword.array.network.rpc.protocol;

import com.zq.sword.array.network.rpc.framework.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.client.RpcClient;
import com.zq.sword.array.network.rpc.framework.handler.TransferHandler;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.network.rpc.protocol.processor.PiperNameProcessor;
import com.zq.sword.array.tasks.Actuator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: piper与namer通信的客户端
 * @author: zhouqi1
 * @create: 2019-04-24 20:08
 **/
public class PiperNameProtocol implements Actuator{

    /**
     * 请求piperNamer的客户端
     */
    private RpcClient rpcClient;

    /**
     * Job命令处理器
     */
    private PiperNameProcessor piperNameProcessor;

    public PiperNameProtocol(String namerLocation) {
        String[] ps = namerLocation.split(":");
        rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
        rpcClient.registerTransferHandler(new Piper2NamerMsgHandler());
    }

    /**
     * 添加Job任务命令处理器
     * @param piperNameProcessor
     */
    public void setPiperNameProcessor(PiperNameProcessor piperNameProcessor){
        this.piperNameProcessor = piperNameProcessor;
    }

    /**
     * 向namer注册piper
     * @param namePiper
     */
    public void registerPiper(NamePiper namePiper){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.REGISTER_PIPER_REQ.value());
        message.setHeader(header);
        message.setBody(namePiper);
        rpcClient.write(message);
    }

    /**
     * 请求Job命令
     * @param namePiper
     */
    public void reqJobCommand(NamePiper namePiper){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.JOB_COMMAND_REQ.value());
        message.setHeader(header);
        message.setBody(namePiper);
        rpcClient.write(message);
    }

    /**
     * 汇报Job健康状态
     * @param health
     */
    public void reportJobHealth(JobHealth health){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.REPORT_JOB_HEALTH.value());
        message.setHeader(header);
        message.setBody(health);
        rpcClient.write(message);
    }

    @Override
    public void start() {
        rpcClient.connect();
    }

    @Override
    public void stop() {
        rpcClient.disconnect();
    }

    /**
     * 发送数据到远程broker
     */
    @ChannelHandler.Sharable
    private class Piper2NamerMsgHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(Piper2NamerMsgHandler.class);

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
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.JOB_COMMAND_RESP.value()) {
                JobCommand jobCommand = (JobCommand)message.getBody();
                if(jobCommand == null){
                    return;
                }
                //监听器接收Job命令
                piperNameProcessor.acceptJobCommand(jobCommand);
            }else {
                ctx.fireChannelRead(msg);
            }
        }
    }
}
