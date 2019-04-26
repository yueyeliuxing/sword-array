package com.zq.sword.array.network.rpc.message;

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
    ;

    MessageType(byte value) {
        this.value = value;
    }

    private byte value;

    public byte value(){
        return value;
    }
}
