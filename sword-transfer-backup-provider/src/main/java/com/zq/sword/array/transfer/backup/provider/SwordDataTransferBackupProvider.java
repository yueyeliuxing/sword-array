package com.zq.sword.array.transfer.backup.provider;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.structure.queue.DataQueue;
import com.zq.sword.array.mq.jade.RightRandomQueue;
import com.zq.sword.array.transfer.server.DefaultTransferServer;
import com.zq.sword.array.transfer.server.TransferServer;

/**
 * @program: sword-array
 * @description: 数据传输提供者
 * @author: zhouqi1
 * @create: 2018-10-24 16:56
 **/
public class SwordDataTransferBackupProvider implements DataTransferBackupProvider {

    private TransferServer transferServer;

    private SwordDataTransferBackupProvider(int port, RightRandomQueue<SwordData> rightRandomQueue, DataQueue<SwordData> leftOrderlyQueue) {
        transferServer = new DefaultTransferServer(port);
        transferServer.registerTransferHandler(new ProvideSwordDataTransferBackupHandler(rightRandomQueue, leftOrderlyQueue));
    }

    public static class SwordDataTransferBackupProviderBuilder {
        private int port;
        private RightRandomQueue<SwordData> rightRandomQueue;
        private DataQueue<SwordData> leftOrderlyQueue;

        public static SwordDataTransferBackupProviderBuilder create(){
            return new SwordDataTransferBackupProviderBuilder();
        }

        public SwordDataTransferBackupProviderBuilder bind(int port){
            this.port = port;
            return this;
        }

        public SwordDataTransferBackupProviderBuilder bindingDataSource(RightRandomQueue<SwordData> rightRandomQueue, DataQueue<SwordData> leftOrderlyQueue){
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
