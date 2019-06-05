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
public class NamerServiceProtocol implements Protocol {

    /**
     * piper服务器 监听外来请求发送数据
     */
    protected NettyRpcServer rpcServer;

    public NamerServiceProtocol(String piperLocation) {
        String[] params = piperLocation.split(":");
        this.rpcServer = new NettyRpcServer(Integer.parseInt(params[1]));
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
        rpcServer.registerProtocolProcessor(namerServiceProcessor);
    }

    @Override
    public void stop() {
        rpcServer.close();
    }

}
