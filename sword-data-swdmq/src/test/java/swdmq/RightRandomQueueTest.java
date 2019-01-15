package com.zq.sword.array.data.swdmq;

import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.swdmq.bitcask.BitcaskRightRandomQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    public void push() throws IOException {
        SwordData swordData = new SwordData();
        swordData.setId(7L);
        SwordCommand swordCommand = new SwordCommand();
        swordCommand.setType((byte)1);
        swordCommand.setKey("user".getBytes());
        swordCommand.setValue("123".getBytes());
        swordData.setValue(swordCommand);
        swordData.setTimestamp(System.currentTimeMillis());
        swordData.setCrc("112");
        rightRandomQueue.push(swordData);

        List<SwordData> swordDatas = rightRandomQueue.selectAfterId(7L);
        System.out.println(swordDatas);
        System.in.read();
    }

    @Test
    public void pollAfterId() {
    }

    @Test
    public void pollAfterId1() {
    }
}