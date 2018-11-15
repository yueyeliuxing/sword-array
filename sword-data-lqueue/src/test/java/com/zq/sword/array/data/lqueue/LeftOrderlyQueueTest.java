package com.zq.sword.array.data.lqueue;

import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.bitcask.BitcaskLeftOrderlyQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class LeftOrderlyQueueTest {

    private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

    @Before
    public void setUp() throws Exception {
        leftOrderlyQueue = new BitcaskLeftOrderlyQueue("E:\\sword\\left\\data");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getLastDataId() {
    }

    @Test
    public void push() throws IOException {
        for(int i = 1; i< 10; i++){
            SwordData orderSwordData = new SwordData();
            orderSwordData.setId(Long.valueOf(i));
            SwordCommand swordCommand = new SwordCommand();
            swordCommand.setType((byte)1);
            swordCommand.setKey(("user_"+i).getBytes());
            swordCommand.setValue(("7852sff_"+i).getBytes());
            orderSwordData.setValue(swordCommand);
            orderSwordData.setTimestamp(System.currentTimeMillis());
            orderSwordData.setCrc("11212");
            boolean isSucess = false;
            while (!isSucess){
                isSucess = leftOrderlyQueue.push(orderSwordData);
            }
            SwordData swordData = null;
            while (swordData == null){
                swordData = leftOrderlyQueue.poll();
            }

            System.out.println(swordData);
        }

        System.in.read();

    }

    @Test
    public void poll() {
    }

    @Test
    public void containsConsumed() {
    }

    @Test
    public void pollAfterId() {
    }

    @Test
    public void pollAfterId1() {
    }
}