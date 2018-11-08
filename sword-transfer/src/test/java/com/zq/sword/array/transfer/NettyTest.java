package com.zq.sword.array.transfer;

import com.zq.sword.array.transfer.client.DefaultTransferClient;
import com.zq.sword.array.transfer.client.TransferClient;
import com.zq.sword.array.transfer.server.DefaultTransferServer;
import com.zq.sword.array.transfer.server.TransferServer;
import org.junit.Test;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2018-11-05 16:17
 **/
public class NettyTest {

    @Test
    public void testServer(){
        TransferServer transferServer = new DefaultTransferServer(8975);
        transferServer.start();
    }

    @Test
    public void testClient()throws Exception{
        TransferClient transferClient = new DefaultTransferClient("127.0.0.1", 8975);
        transferClient.connect();
        System.in.read();
    }
}
