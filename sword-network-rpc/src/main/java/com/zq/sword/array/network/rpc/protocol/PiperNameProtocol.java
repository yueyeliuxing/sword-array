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
public class PiperNameProtocol implements Protocol {

    /**
     * 请求piperNamer的客户端
     */
    private RpcClient rpcClient;

    public PiperNameProtocol(String namerLocation) {
        String[] ps = namerLocation.split(":");
        rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
    }

    /**
     * 添加Job任务命令处理器
     * @param piperNameProcessor
     */
    public void setPiperNameProcessor(PiperNameProcessor piperNameProcessor){
        rpcClient.registerProtocolProcessor(piperNameProcessor);
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
        rpcClient.start();
    }

    @Override
    public void stop() {
        rpcClient.close();
    }
}
