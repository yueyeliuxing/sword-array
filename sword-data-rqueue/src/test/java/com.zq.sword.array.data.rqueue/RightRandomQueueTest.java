package com.zq.sword.array.data.rqueue;

import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.data.rqueue.bitcask.BitcaskRightRandomQueue;
import com.zq.sword.array.common.data.SwordData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RightRandomQueueTest {

    private RightRandomQueue rightRandomQueue;

    @Before
    public void setUp() throws Exception {
        rightRandomQueue = new BitcaskRightRandomQueue("E:\\sword\\right\\data", "E:\\sword\\right\\index");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void registerSwordDataListener() {
    }

    @Test
    public void getLastSwordDataId() {
    }

    @Test
    public void push() {
        SwordData swordData = new SwordData();
        swordData.setId(2L);
        SwordCommand swordCommand = new SwordCommand();
        swordCommand.setType((byte)1);
        swordCommand.setKey("user");
        swordCommand.setValue("123");
        swordData.setValue(swordCommand);
        swordData.setTimestamp(System.currentTimeMillis());
        swordData.setCrc("112");
        rightRandomQueue.push(swordData);

        List<SwordData> swordDatas = rightRandomQueue.pollAfterId(2L);
        System.out.println(swordDatas);
    }

    @Test
    public void pollAfterId() {
    }

    @Test
    public void pollAfterId1() {
    }
}