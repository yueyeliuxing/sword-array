package com.zq.sword.array.transfer.backup.provider;

/**
 * @program: sword-array
 * @description: 数据传输提供者
 * @author: zhouqi1
 * @create: 2018-10-24 16:53
 **/
public interface DataTransferBackupProvider {

    /**
     * 提供者启动
     */
    void start();

    /**
     * 提供者关闭
     */
    void stop();
}
