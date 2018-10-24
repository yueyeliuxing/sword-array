package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;
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
public class BitcaskLeftOrderlyQueue implements LeftOrderlyQueue<SwordData> {

    private Logger logger = LoggerFactory.getLogger(BitcaskLeftOrderlyQueue.class);

    private volatile QueueState state;

    private OrderSwordDataProcessor orderSwordDataProcessor;

    private DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge;


    public BitcaskLeftOrderlyQueue(String dataFilePath){
        state = QueueState.NEW;

        orderSwordDataProcessor = new OrderSwordDataProcessor(dataFilePath, this);
        orderSwordDataProcessor.start();

        state = QueueState.START;
    }

    public void setDataCycleDisposeBridge(DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge) {
        this.dataCycleDisposeBridge = dataCycleDisposeBridge;
    }

    @Override
    public Long getLastDataId() {
        return orderSwordDataProcessor.getLastDataId();
    }

    @Override
    public boolean push(SwordData swordData) {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }
        orderSwordDataProcessor.addSwordData(swordData);
        return true;
    }

    @Override
    public SwordData poll() {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能获取数据", state.name());
            return null;
        }

        SwordData swordData = orderSwordDataProcessor.pollSwordData();
        if(swordData != null && dataCycleDisposeBridge != null){
            dataCycleDisposeBridge.addCycleData(swordData.getValue());
        }
        return swordData;
    }

    @Override
    public List<SwordData> pollAfterId(Long id) {
        return pollAfterId(id, null);
    }

    @Override
    public List<SwordData> pollAfterId(Long id, Integer maxNum) {
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
