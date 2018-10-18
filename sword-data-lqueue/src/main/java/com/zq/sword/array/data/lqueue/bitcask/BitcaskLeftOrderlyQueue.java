package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.QueueState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: bitcask类型队列
 * @author: zhouqi1
 * @create: 2018-10-17 19:12
 **/
public class BitcaskLeftOrderlyQueue implements LeftOrderlyQueue<OrderSwordData> {

    private Logger logger = LoggerFactory.getLogger(BitcaskLeftOrderlyQueue.class);

    private volatile QueueState state;

    private OrderSwordDataProcessor orderSwordDataProcessor;

    private ConsumedSwordDataProcessor consumedSwordDataProcessor;


    public BitcaskLeftOrderlyQueue(String dataFilePath){
        state = QueueState.NEW;

        orderSwordDataProcessor = new OrderSwordDataProcessor(dataFilePath, this);
        orderSwordDataProcessor.start();

        consumedSwordDataProcessor = new ConsumedSwordDataProcessor();
        consumedSwordDataProcessor.start();

        state = QueueState.START;
    }

    @Override
    public Long getLastDataId() {
        return orderSwordDataProcessor.getLastDataId();
    }

    @Override
    public boolean push(OrderSwordData orderSwordData) {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }
        orderSwordDataProcessor.addOrderSwordData(orderSwordData);
        return true;
    }

    @Override
    public OrderSwordData poll() {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能获取数据", state.name());
            return null;
        }

        OrderSwordData orderSwordData = orderSwordDataProcessor.pollOrderSwordData();
        if(orderSwordData != null){
            consumedSwordDataProcessor.addOrderSwordData(orderSwordData);
        }
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

    @Override
    public QueueState state() {
        return state;
    }

    public void setState(QueueState state){
        this.state = state;
    }
}
