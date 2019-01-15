package com.zq.sword.array.transfer.backup.gather;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.queue.DataQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.transfer.client.DefaultTransferClient;
import com.zq.sword.array.transfer.client.TransferClient;

/**
 * @program: sword-array
 * @description: sword 数据传输收集者
 * @author: zhouqi1
 * @create: 2018-10-24 15:42
 **/
public class SwordDataTransferBackupGather implements DataTransferBackupGather {

    private TransferClient transferClient;

    private SwordDataTransferBackupGather(String host, int poryt, DataQueue<SwordData> leftOrderlyQueue, RightRandomQueue<SwordData> rightRandomQueue){
        this.transferClient = getTransferClient(host, poryt, leftOrderlyQueue, rightRandomQueue);
    }

    private TransferClient getTransferClient(String host, int port,
                                             DataQueue<SwordData> leftOrderlyQueue,
                                             RightRandomQueue<SwordData> rightRandomQueue){
        TransferClient transferClient = new DefaultTransferClient(host, port);
        transferClient.registerTransferHandler(new GatherSwordDataTransferBackupHandler(rightRandomQueue, leftOrderlyQueue));
        return transferClient;
    }

    public static class SwordDataTransferBackupGatherBuilder {

        private String host;

        private int port;

        private DataQueue<SwordData> leftOrderlyQueue;

        private RightRandomQueue<SwordData> rightRandomQueue;

        public static SwordDataTransferBackupGatherBuilder create(){
            return new SwordDataTransferBackupGatherBuilder();
        }

        public SwordDataTransferBackupGatherBuilder connect(String host, int port){
            this.host = host;
            this.port = port;
            return this;
        }
        public SwordDataTransferBackupGatherBuilder bindingTargetDataSource(DataQueue<SwordData> leftOrderlyQueue, RightRandomQueue<SwordData> rightRandomQueue){
            this.leftOrderlyQueue = leftOrderlyQueue;
            this.rightRandomQueue = rightRandomQueue;
            return this;
        }

        public SwordDataTransferBackupGather build(){
            return new SwordDataTransferBackupGather(host, port, leftOrderlyQueue, rightRandomQueue);
        }
    }

    @Override
    public void start() {
        if(transferClient == null){
            throw new NullPointerException("transferClient");
        }
        transferClient.connect();
    }

    @Override
    public void stop() {
        if(transferClient == null){
            throw new NullPointerException("transferClient");
        }
        transferClient.disconnect();
    }
}
