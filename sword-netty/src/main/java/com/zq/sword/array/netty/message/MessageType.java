package com.zq.sword.array.netty.message;

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

    POLL_T_LEFT_DATA_TRANSFER_REQ((byte)7),
    POLL_T_LEFT_DATA_TRANSFER_RESP((byte)8),

    PUSH_T_RIGHT_DATA_TRANSFER_REQ((byte)9),
    PUSH_T_RIGHT_DATA_TRANSFER_RESP((byte)10),

    POLL_T_RIGHT_DATA_BACKUP_REQ((byte)11),
    POLL_T_RIGHT_DATA_BACKUP_RESP((byte)12),

    POLL_T_LEFT_DATA_BACKUP_REQ((byte)13),
    POLL_T_LEFT_DATA_BACKUP_RESP((byte)14),
    ;

    MessageType(byte value) {
        this.value = value;
    }

    private byte value;

    public byte value(){
        return value;
    }
}
