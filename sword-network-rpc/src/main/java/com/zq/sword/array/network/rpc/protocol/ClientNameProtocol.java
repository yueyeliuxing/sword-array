package com.zq.sword.array.network.rpc.protocol;

import com.zq.sword.array.network.rpc.framework.client.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.client.RpcClient;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.network.rpc.protocol.processor.ClientNameProcessor;
import com.zq.sword.array.network.rpc.protocol.processor.PiperNameProcessor;

/**
 * @program: sword-array
 * @description: piper与namer通信的客户端
 * @author: zhouqi1
 * @create: 2019-04-24 20:08
 **/
public class ClientNameProtocol implements Protocol {

    /**
     * 请求piperNamer的客户端
     */
    private RpcClient rpcClient;

    public ClientNameProtocol(String namerLocation) {
        String[] ps = namerLocation.split(":");
        rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
    }

    /**
     * 添加Job任务命令处理器
     * @param clientNameProcessor
     */
    public void setClientNameProcessor(ClientNameProcessor clientNameProcessor){
        rpcClient.registerProtocolProcessor(clientNameProcessor);
    }

    /**
     * 客户端查询piper数据
     */
    public void sendSearchPiper(){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_PIPERS.value());
        message.setHeader(header);
        message.setBody(null);
        rpcClient.write(message);
    }

    /**
     * 客户端创建job
     * @param nameJob
     */
    public void createJob(NameJob nameJob){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_CREATE_JOB.value());
        message.setHeader(header);
        message.setBody(nameJob);
        rpcClient.write(message);
    }
    /**
     * 客户端开启job
     * @param jobName
     */
    public void startJob(String jobName){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_START_JOB.value());
        message.setHeader(header);
        message.setBody(jobName);
        rpcClient.write(message);
    }

    /**
     * 客户端暂停job
     * @param jobName
     */
    public void stopJob(String jobName){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_STOP_JOB.value());
        message.setHeader(header);
        message.setBody(jobName);
        rpcClient.write(message);
    }

    /**
     * 客户端移除job
     * @param jobName
     */
    public void removeJob(String jobName){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_REMOVE_JOB.value());
        message.setHeader(header);
        message.setBody(jobName);
        rpcClient.write(message);
    }

    /**
     * 查询指定job
     * @param jobName
     */
    public void sendSearchJob(String jobName){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_JOB.value());
        message.setHeader(header);
        message.setBody(jobName);
        rpcClient.write(message);
    }

    /**
     * 查询指定 分支job
     * @param jobName
     */
    public void sendSearchBranchJob(String jobName){
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_BRANCH_JOB.value());
        message.setHeader(header);
        message.setBody(jobName);
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
