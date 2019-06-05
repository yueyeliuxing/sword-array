package com.zq.sword.array.network.rpc.framework.handler;

import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 传输执行器接口
 * @author: zhouqi1
 * @create: 2018-08-01 20:29
 **/
public class ProtocolHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(ProtocolHandler.class);

    private List<ProtocolProcessor> protocolProcessors;

    public ProtocolHandler(List<ProtocolProcessor> protocolProcessors) {
        this.protocolProcessors = protocolProcessors;
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
        TransferMessage message = (TransferMessage)msg;
        logger.info("receive msg request : {}", message);
        boolean isFilter = false;
        if(protocolProcessors != null && !protocolProcessors.isEmpty()){
            for (ProtocolProcessor protocolProcessor : protocolProcessors){
                if(protocolProcessor.canProcess(message)){
                    isFilter = true;
                    ctx.writeAndFlush(protocolProcessor.process(message));
                }
            }
        }
        if(!isFilter){
            ctx.fireChannelRead(msg);
        }
    }
}
