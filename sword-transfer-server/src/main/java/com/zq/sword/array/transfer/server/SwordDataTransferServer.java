package com.zq.sword.array.transfer.server;

import com.zq.sword.array.common.data.SwordData;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.netty.server.DefaultTransferServer;
import com.zq.sword.array.netty.server.TransferServer;
import com.zq.sword.array.transfer.server.handler.PushTransferDataItemHandler;

/**
 * @program: sword-array
 * @description: 数据传输服务器
 * @author: zhouqi1
 * @create: 2018-10-19 13:41
 **/
public class SwordDataTransferServer implements DataTransferServer{

    private TransferServer transferServer;

    private RightRandomQueue<SwordData> rightRandomQueue;

    public SwordDataTransferServer(String ip, int port, RightRandomQueue<SwordData> rightRandomQueue) {
        transferServer = new DefaultTransferServer(ip, port);
        transferServer.registerTransferHandler(new PushTransferDataItemHandler(rightRandomQueue));

        this.rightRandomQueue = rightRandomQueue;
    }

    @Override
    public void start() {
        transferServer.start();
    }
}
