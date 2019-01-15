package com.zq.sword.array.data.bridge;


import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger = LoggerFactory.getLogger(SwordCommandCycleDisposeBridge.class);

    private Set<SwordCommand> consumedSwordDataSet;

    private TaskExecutor taskExecutor;

    public SwordCommandCycleDisposeBridge() {
        logger.info("SwordCommandCycleDisposeBridge 模块启动成功");
        consumedSwordDataSet = new CopyOnWriteArraySet<>();
        taskExecutor = new SingleTaskExecutor();
    }

    public void start(){
        startTasks();
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //只保存两分钟的数据
        taskExecutor.timedExecute(()->{
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
