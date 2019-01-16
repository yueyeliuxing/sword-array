package com.zq.sword.array.transfer.gather;

import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.structure.queue.DataQueue;
import com.zq.sword.array.data.structure.queue.StoredWrapDataQueue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DataTransferGatherTest {

    private DataTransferGather dataTransferGather;

    @Before
    public void setUp() throws Exception {
        DataQueue<SwordData> leftOrderlyQueue = new StoredWrapDataQueue("E:\\sword\\left\\data");
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