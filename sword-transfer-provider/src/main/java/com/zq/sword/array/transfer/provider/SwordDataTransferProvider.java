package com.zq.sword.array.transfer.provider;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.transfer.server.DefaultTransferServer;
import com.zq.sword.array.transfer.server.TransferServer;

/**
 * @program: sword-array
 * @description: 数据传输提供者
 * @author: zhouqi1
 * @create: 2018-10-24 16:56
 **/
public class SwordDataTransferProvider implements DataTransferProvider {

    private TransferServer transferServer;

    private SwordDataTransferProvider(int port, RightRandomQueue<SwordData> rightRandomQueue) {
        transferServer = new DefaultTransferServer(port);
        transferServer.registerTransferHandler(new ProvideSwordDataTransferHandler(rightRandomQueue));
    }

    public static class SwordDataTransferProviderBuilder {
        private int port;
        private RightRandomQueue<SwordData> rightRandomQueue;

        public static SwordDataTransferProviderBuilder create(){
            return new SwordDataTransferProviderBuilder();
        }

        public SwordDataTransferProviderBuilder bind(int port){
            this.port = port;
            return this;
        }

        public SwordDataTransferProviderBuilder bindingDataSource(RightRandomQueue<SwordData> rightRandomQueue){
            this.rightRandomQueue = rightRandomQueue;
            return this;
        }

        public SwordDataTransferProvider build(){
            return new SwordDataTransferProvider(port, rightRandomQueue);
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
