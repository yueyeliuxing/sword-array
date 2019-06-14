package com.zq.sword.array.network.rpc.framework.protocol.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: netty消息
 * @author: zhouqi1
 * @create: 2018-07-06 10:37
 **/
@Data
@ToString
public final class TransferMessage implements Serializable {

    private static final long serialVersionUID = -2547176236114990872L;
    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private Object body;

}
