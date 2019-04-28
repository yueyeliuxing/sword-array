package com.zq.sword.array.piper.job;

import java.util.List;

/**
 * @program: sword-array
 * @description: 写入任务
 * @author: zhouqi1
 * @create: 2019-04-25 15:54
 **/
public interface WriteTask extends Task{

    /**
     * 刷新任务消费piper
     * @param incrementConsumePipers
     * @param decreaseConsumePipers
     */
    void flushJobConsumePipers(List<String> incrementConsumePipers, List<String> decreaseConsumePipers);
}
