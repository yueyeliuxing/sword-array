package com.zq.sword.array.transfer;

import com.zq.sword.array.network.rpc.framework.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.RpcClient;
import com.zq.sword.array.network.rpc.framework.NettyRpcServer;
import com.zq.sword.array.network.rpc.framework.RpcServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2018-11-05 16:17
 **/
public class NettyTest {

    @Test
    public void testServer(){
        Executors.newSingleThreadExecutor();
        RpcServer transferServer = new NettyRpcServer(8975);
        transferServer.start();
    }

    @Test
    public void testClient()throws Exception{
        RpcClient transferClient = new NettyRpcClient("127.0.0.1", 8975);
        transferClient.start();
        System.in.read();
    }

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = null;
        Bootstrap bootstrap = null;
        NioServerSocketChannel nioServerSocketChannel = null;
        NioSocketChannel nioSocketChannel = null;

        SocketChannel socketChannel = null;
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
    }
}
