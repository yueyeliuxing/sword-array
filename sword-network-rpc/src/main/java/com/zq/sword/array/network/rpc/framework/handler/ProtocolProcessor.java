package com.zq.sword.array.network.rpc.framework.handler;

import com.zq.sword.array.network.rpc.framework.message.TransferMessage;

/**
 * @program: sword-array
 * @description: 连接处理器
 * @author: zhouqi1
 * @create: 2019-06-04 17:45
 **/
public interface ProtocolProcessor {

    /**
     * 是否符合处理规则要求
     * @param message
     * @return
     */
    boolean canProcess(TransferMessage message);

    /**
     * 处理器
     * @param message
     */
    TransferMessage process(TransferMessage message);
}
