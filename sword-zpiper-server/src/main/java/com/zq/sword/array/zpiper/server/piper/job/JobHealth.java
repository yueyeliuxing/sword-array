package com.zq.sword.array.zpiper.server.piper.job;

/**
 * @program: sword-array
 * @description: Job健康度
 * @author: zhouqi1
 * @create: 2019-04-25 16:03
 **/
public class JobHealth extends TaskHealth {

    /**
     * 复制任务健康度
     */
    private TaskHealth replicateTaskHealth;

    /**
     * 写入任务健康度
     */
    private TaskHealth writeTaskHealth;

    public JobHealth(String name, int state, TaskHealth replicateTaskHealth, TaskHealth writeTaskHealth) {
        super(name, state);
        this.replicateTaskHealth = replicateTaskHealth;
        this.writeTaskHealth = writeTaskHealth;
    }
}
