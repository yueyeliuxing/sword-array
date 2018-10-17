package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;

import java.util.List;

/**
 * @program: sword-array
 * @description: bitcask类型队列
 * @author: zhouqi1
 * @create: 2018-10-17 19:12
 **/
public class BitcaskLeftOrderlyQueue implements LeftOrderlyQueue<OrderSwordData> {

    private OrderSwordDataProcessor orderSwordDataProcessor;

    private ConsumedSwordDataProcessor consumedSwordDataProcessor;


    public BitcaskLeftOrderlyQueue(String dataFilePath){
        orderSwordDataProcessor = new OrderSwordDataProcessor(dataFilePath);
        orderSwordDataProcessor.start();

        consumedSwordDataProcessor = new ConsumedSwordDataProcessor();
        consumedSwordDataProcessor.start();
    }

    @Override
    public Long getLastDataId() {
        return orderSwordDataProcessor.getLastDataId();
    }

    @Override
    public void push(OrderSwordData orderSwordData) {
        orderSwordDataProcessor.addOrderSwordData(orderSwordData);
    }

    @Override
    public OrderSwordData poll() {
        OrderSwordData orderSwordData = orderSwordDataProcessor.pollOrderSwordData();
        consumedSwordDataProcessor.addOrderSwordData(orderSwordData);
        return orderSwordData;
    }

    @Override
    public boolean containsConsumed(OrderSwordData data) {
        return consumedSwordDataProcessor.containsOrderSwordData(data);
    }

    @Override
    public List<OrderSwordData> pollAfterId(Long id) {
        return pollAfterId(id, null);
    }

    @Override
    public List<OrderSwordData> pollAfterId(Long id, Integer maxNum) {
        return orderSwordDataProcessor.pollAfterId(id, maxNum);
    }
}
