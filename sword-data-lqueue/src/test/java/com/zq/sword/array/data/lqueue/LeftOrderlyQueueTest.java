package com.zq.sword.array.data.lqueue;

import com.zq.sword.array.data.lqueue.bitcask.BitcaskLeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.bitcask.OrderSwordData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LeftOrderlyQueueTest {

    private LeftOrderlyQueue<OrderSwordData> leftOrderlyQueue;

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
    public void push() {
        OrderSwordData orderSwordData = new OrderSwordData();
        orderSwordData.setId(2L);
        orderSwordData.setValue("11212");
        orderSwordData.setTimestamp(System.currentTimeMillis());
        orderSwordData.setCrc("11212");
        leftOrderlyQueue.push(orderSwordData);

        orderSwordData = leftOrderlyQueue.poll();
        System.out.println(orderSwordData);
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