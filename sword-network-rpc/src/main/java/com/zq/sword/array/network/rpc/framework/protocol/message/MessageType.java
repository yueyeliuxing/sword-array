package com.zq.sword.array.network.rpc.framework.protocol.message;

public enum MessageType {
    /**
     * 消息类型
     *  0 业务请求消息
     *  1 业务响应消息
     *  2 业务ONE WAY 消息（即是请求又是响应消息）
     *  3 握手请求消息
     *  4 握手应答消息
     *  5 心跳请求消息
     *  6 心跳应答消息
     */

    BUSINESS_REQ((byte)0),
    BUSINESS_RESP((byte)0),
    BUSINESS_ONE_WAY((byte)0),

    LOGIN_REQ((byte)3),
    LOGIN_RESP((byte)4),
    HEARTBEAT_REQ((byte)5),
    HEARTBEAT_RESP((byte)6),

    SEND_REPLICATE_DATA_REQ((byte)7),
    SEND_REPLICATE_DATA_RESP((byte)8),

    RECEIVE_REPLICATE_DATA_REQ((byte)9),
    RECEIVE_REPLICATE_DATA_RESP((byte)10),

    SEND_CONSUME_NEXT_OFFSET_REQ((byte)7),
    SEND_CONSUME_NEXT_OFFSET_RESP((byte)8),

    JOB_COMMAND_REQ((byte)11),
    JOB_COMMAND_RESP((byte)12),

    REGISTER_PIPER_REQ((byte)11),
    REGISTER_PIPER_RESP((byte)12),

    REPORT_JOB_HEALTH((byte)13),

    /**
     * 客户端创建一个任务
     */
    CLIENT_CREATE_JOB((byte)14),

    /**
     * 客户端开启一个任务
     */
    CLIENT_START_JOB((byte)15),

    /**
     * 客户端暂停一个任务
     */
    CLIENT_STOP_JOB((byte)15),


    /**
     * 客户端删除一个任务
     */
    CLIENT_REMOVE_JOB((byte)14),

    /**
     * 客户端查询piper
     */
    CLIENT_SEARCH_PIPERS((byte)15),

    /**
     * 客户端查询piper结果
     */
    CLIENT_SEARCH_PIPERS_RESULT((byte)16),

    /**
     * 客户端查询job
     */
    CLIENT_SEARCH_JOB((byte)17),

    /**
     * 客户端查询job结果
     */
    CLIENT_SEARCH_JOB_RESULT((byte)18),

    /**
     * 客户端查询 分支job
     */
    CLIENT_SEARCH_BRANCH_JOB((byte)19),

    /**
     * 客户端查询 分支job结果
     */
    CLIENT_SEARCH_BRANCH_JOB_RESULT((byte)20),
    ;

    MessageType(byte value) {
        this.value = value;
    }

    private byte value;

    public byte value(){
        return value;
    }
}
