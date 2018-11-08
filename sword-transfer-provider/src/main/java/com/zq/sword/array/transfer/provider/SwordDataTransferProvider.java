package com.zq.sword.array.transfer.provider;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.DataQueue;
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

    private SwordDataTransferProvider(int port, DataQueue<SwordData> dataQueue) {
        transferServer = new DefaultTransferServer(port);
        transferServer.registerTransferHandler(new ProvideSwordDataTransferHandler(dataQueue));
    }

    public static class SwordDataTransferProviderBuilder {
        private int port;
        private DataQueue<SwordData> dataQueue;

        public static SwordDataTransferProviderBuilder create(){
            return new SwordDataTransferProviderBuilder();
        }

        public SwordDataTransferProviderBuilder bind(int port){
            this.port = port;
            return this;
        }

        public SwordDataTransferProviderBuilder bindingDataSource(DataQueue<SwordData> dataQueue){
            this.dataQueue = dataQueue;
            return this;
        }

        public SwordDataTransferProvider build(){
            return new SwordDataTransferProvider(port, dataQueue);
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
