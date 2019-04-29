package com.zq.sword.array.network.rpc.protocol.dto.piper.command;

/**
 * @program: sword-array
 * @description: 任务类型
 * @author: zhouqi1
 * @create: 2019-04-24 17:22
 **/
public enum JobType {

    /**
     * 创建
     */
    JOB_NEW((byte)0),

    /**
     * 启动
     */
    JOB_START((byte)1),

    /**
     * 销毁
     */
    JOB_DESTROY((byte)2),

    /**
     * 重启ReplicateTask
     */
    REPLICATE_TASK_RESTART((byte)3),

    /**
     * 重启WriteTask
     */
    WRITE_TASK_RESTART((byte)4),

    /**
     * 增加任务复制piper
     */
    BACKUP_PIPERS_CHANGE((byte)5),

    /**
     * 消费piper改变
     */
    CONSUME_PIPERS_CHANGE((byte)6),
    ;

    private byte type;

    JobType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public static JobType toType(byte type) {
        for (JobType jobType : values()) {
            if (jobType.getType() == type) {
                return jobType;
            }
        }
        return null;
    }

}
