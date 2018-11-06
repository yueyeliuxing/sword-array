package com.zq.sword.array.transfer.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 消息头
 * @author: zhouqi1
 * @create: 2018-07-05 21:15
 **/
@Data
@ToString
public final class Header implements Serializable{

    private static final long serialVersionUID = 5319810025738100217L;
    /**
     *  0xABEF + 主版本号 + 次版本号
     */
    private int crcCode = 0xabef0101;

    /**
     * 消息长度 包括消息头和消息体
     */
    private int length;

    /**
     * 集群节点内全局唯一，由会话生成器生成
     */
    private long sessionID;

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
    private byte type;

    /**
     * 消息优先级 0~255
     */
    private byte priority;

    /**
     * 可选字段，用于扩展消息头
     */
    private Map<String, Object> attachment = new HashMap<>();


}
