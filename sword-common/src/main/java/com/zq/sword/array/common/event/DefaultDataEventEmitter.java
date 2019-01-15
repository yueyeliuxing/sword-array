package com.zq.sword.array.common.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: 默认的事件发射器
 * @author: zhouqi1
 * @create: 2019-01-14 09:57
 **/
public class DefaultDataEventEmitter implements DataEventEmitter {

    /**
     * 事件监听器集合
     */
    private List<DataEventListener> dataEventListeners;

    public DefaultDataEventEmitter() {
        this.dataEventListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerEventListener(DataEventListener dataEventListener) {
        this.dataEventListeners.add(dataEventListener);
    }

    @Override
    public void removeEventListener(DataEventListener dataEventListener) {
        this.dataEventListeners.remove(dataEventListener);
    }


    public void emitter(DataEvent dataEvent) {
        if(dataEventListeners != null && !dataEventListeners.isEmpty()){
            dataEventListeners.forEach(c->c.listen(dataEvent));
        }
    }
}
