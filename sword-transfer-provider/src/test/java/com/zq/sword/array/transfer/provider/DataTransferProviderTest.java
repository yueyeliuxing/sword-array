package com.zq.sword.array.transfer.provider;

import com.zq.sword.array.mq.jade.bitcask.BitcaskRightRandomQueue;
import com.zq.sword.array.transfer.client.DefaultTransferClient;
import com.zq.sword.array.transfer.client.TransferClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataTransferProviderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void startServer() throws Exception{
        RightRandomQueue<SwordData> rightRandomQueue = new BitcaskRightRandomQueue("E:\\sword\\right\\data", "E:\\sword\\right\\index");
        //初始化 数据传输提供者
        DataTransferProvider dataTransferProvider = SwordDataTransferProvider.SwordDataTransferProviderBuilder.create()
                .bindingDataSource(rightRandomQueue)
                .bind(8990)
                .build();
        dataTransferProvider.start();
        System.in.read();
    }

    @Test
    public void getData() throws Exception{
        TransferClient transferClient = new DefaultTransferClient("127.0.0.1", 8990);
        transferClient.registerTransferHandler(new GatherSwordDataTransferHandler());
        transferClient.connect();
        System.in.read();
    }

    @Test
    public void stop() {
    }
}