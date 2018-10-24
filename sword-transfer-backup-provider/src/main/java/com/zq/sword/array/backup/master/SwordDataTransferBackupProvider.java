package com.zq.sword.array.backup.master;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.netty.server.DefaultTransferServer;
import com.zq.sword.array.netty.server.TransferServer;

/**
 * @program: sword-array
 * @description: 数据传输提供者
 * @author: zhouqi1
 * @create: 2018-10-24 16:56
 **/
public class SwordDataTransferBackupProvider implements DataTransferBackupProvider {

    private TransferServer transferServer;

    private SwordDataTransferBackupProvider(int port, RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue) {
        transferServer = new DefaultTransferServer(port);
        transferServer.registerTransferHandler(new ProvideSwordDataTransferBackupHandler(rightRandomQueue, leftOrderlyQueue));
    }

    public static class SwordDataTransferProviderBuilder {
        private int port;
        private RightRandomQueue<SwordData> rightRandomQueue;
        private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

        public static SwordDataTransferProviderBuilder create(){
            return new SwordDataTransferProviderBuilder();
        }

        public SwordDataTransferProviderBuilder bind(int port){
            this.port = port;
            return this;
        }

        public SwordDataTransferProviderBuilder bindingDataSource(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue){
            this.rightRandomQueue = rightRandomQueue;
            this.leftOrderlyQueue = leftOrderlyQueue;
            return this;
        }

        public SwordDataTransferBackupProvider build(){
            return new SwordDataTransferBackupProvider(port, rightRandomQueue, leftOrderlyQueue);
        }
    }

    @Override
    public void start() {
        transferServer.start();
    }

    @Override
    public void stop() {
        transferServer.shutdown();
    }
}
