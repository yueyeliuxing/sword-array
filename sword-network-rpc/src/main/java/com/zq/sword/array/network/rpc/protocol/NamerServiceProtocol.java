package com.zq.sword.array.network.rpc.protocol;

import com.zq.sword.array.network.rpc.framework.handler.TransferHandler;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.framework.server.NettyRpcServer;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.network.rpc.protocol.processor.NamerServiceProcessor;
import com.zq.sword.array.tasks.Actuator;
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
public class NamerServiceProtocol implements Actuator{

    /**
     * piper服务器 监听外来请求发送数据
     */
    protected NettyRpcServer rpcServer;

    /**
     * piper处理器
     */
    private NamerServiceProcessor namerServiceProcessor;

    public NamerServiceProtocol(String piperLocation) {
        String[] params = piperLocation.split(":");
        this.rpcServer = new NettyRpcServer(Integer.parseInt(params[1]));
        this.rpcServer.registerTransferHandler(new NamerServiceProtocolTransferHandler());
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

    public void setNamerServiceProcessor(NamerServiceProcessor namerServiceProcessor) {
        this.namerServiceProcessor = namerServiceProcessor;
    }

    @Override
    public void stop() {
        rpcServer.shutdown();
    }

    /**
     * 数据传输
     */
    @ChannelHandler.Sharable
    private class NamerServiceProtocolTransferHandler extends TransferHandler {

        private Logger logger = LoggerFactory.getLogger(NamerServiceProtocolTransferHandler.class);

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
            if(message.getHeader() != null && message.getHeader().getType() == MessageType.REGISTER_PIPER_REQ.value()) {
                NamePiper namePiper = (NamePiper)message.getBody();
                namerServiceProcessor.handlePiperRegister(namePiper);
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.JOB_COMMAND_REQ.value()) {
                NamePiper namePiper = (NamePiper)message.getBody();
                JobCommand command = namerServiceProcessor.handleJobCommandReq(namePiper);
                if(command == null){
                    return;
                }
                ctx.writeAndFlush(buildJobCommandResp(command));
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.REPORT_JOB_HEALTH.value()) {
                JobHealth jobHealth = (JobHealth) message.getBody();
                namerServiceProcessor.handleTaskHealthReport(jobHealth);
            }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_START_JOB.value()) {
                NameJob nameJob = (NameJob)message.getBody();
                namerServiceProcessor.handleClientStartJobReq(nameJob);
            }else {
                ctx.fireChannelRead(msg);
            }
        }

        /**
         * 构建JobCommand 返回协议
         * @param command
         * @return
         */
        private TransferMessage buildJobCommandResp(JobCommand command) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.JOB_COMMAND_RESP.value());
            message.setHeader(header);
            message.setBody(command);
            return message;
        }

        private TransferMessage buildReceiveMessageResp(List<Object> msgs) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.RECEIVE_REPLICATE_DATA_RESP.value());
            message.setHeader(header);
            if(msgs != null){
                message.setBody(msgs);
            }
            return message;
        }

        private TransferMessage buildSendReplicateDataResp(Object replicateDataId) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_REPLICATE_DATA_RESP.value());
            message.setHeader(header);
            message.setBody(replicateDataId);
            return message;
        }

        private TransferMessage buildSendConsumeNextOffsetResp(Object consumeNextOffset) {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.SEND_CONSUME_NEXT_OFFSET_RESP.value());
            message.setHeader(header);
            message.setBody(consumeNextOffset);
            return message;
        }
    }
}
