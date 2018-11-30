package com.zq.sword.array.transfer.gather;

import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.bitcask.BitcaskLeftOrderlyQueue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DataTransferGatherTest {

    private DataTransferGather dataTransferGather;

    @Before
    public void setUp() throws Exception {
        LeftOrderlyQueue<SwordData> leftOrderlyQueue = new BitcaskLeftOrderlyQueue("E:\\sword\\left\\data");
        dataTransferGather = new SwordDataTransferGather(IPUtil.getServerIp(), 8990, leftOrderlyQueue);
    }

    @Test
    public void start() throws IOException {
        dataTransferGather.start();
        System.in.read();
    }

    @Test
    public void stop() {
    }
}