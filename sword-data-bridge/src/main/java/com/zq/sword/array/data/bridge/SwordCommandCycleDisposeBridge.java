package com.zq.sword.array.data.bridge;


import com.zq.sword.array.data.SwordCommand;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据循环处理
 * @author: zhouqi1
 * @create: 2018-10-22 16:27
 **/
public class SwordCommandCycleDisposeBridge implements DataCycleDisposeBridge<SwordCommand> {

    private Set<SwordCommand> consumedSwordDataSet;

    private CycleSwordCommandBackgroundExecutor cycleSwordCommandBackgroundExecutor;

    public SwordCommandCycleDisposeBridge() {
        consumedSwordDataSet = new CopyOnWriteArraySet<>();
        cycleSwordCommandBackgroundExecutor = new CycleSwordCommandBackgroundExecutor();
    }

    public void start(){
        startTasks();
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //只保存两分钟的数据
        cycleSwordCommandBackgroundExecutor.timedExecute(()->{
            consumedSwordDataSet.clear();
        }, 2, TimeUnit.MINUTES);
    }


    @Override
    public boolean isCycleData(SwordCommand command) {
        return consumedSwordDataSet.contains(command);
    }

    @Override
    public boolean addCycleData(SwordCommand command) {
        return consumedSwordDataSet.add(command);
    }
}
