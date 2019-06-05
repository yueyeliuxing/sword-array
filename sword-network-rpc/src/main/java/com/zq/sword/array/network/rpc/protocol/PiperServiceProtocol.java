package com.zq.sword.array.network.rpc.protocol;

import com.zq.sword.array.network.rpc.framework.handler.TransferHandler;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.framework.server.NettyRpcServer;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.processor.PiperServiceProcessor;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;
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
public class PiperServiceProtocol implements Protocol {

    /**
     * piper服务器 监听外来请求发送数据
     */
    protected NettyRpcServer rpcServer;

    public PiperServiceProtocol(String piperLocation) {
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

    /**
     * 设置Broker 消息处理器
     * @param piperServiceProcessor
     */
    public void setPiperServiceProcessor(PiperServiceProcessor piperServiceProcessor){
        rpcServer.registerProtocolProcessor(piperServiceProcessor);
    }

    @Override
    public void stop() {
        rpcServer.close();
    }
}
