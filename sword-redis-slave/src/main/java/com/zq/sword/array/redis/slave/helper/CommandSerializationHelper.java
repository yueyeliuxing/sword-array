package com.zq.sword.array.redis.slave.helper;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 命令序列化帮助类
 * @author: zhouqi1
 * @create: 2018-10-10 15:41
 **/
public class CommandSerializationHelper {

    /**
     * 序列化命令内容
     * @param command
     * @return
     */
    public static String serializeCommand(Command command){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if(command instanceof SetCommand){
            SetCommand setCommand = (SetCommand) command;
            byteBuffer.clear();
            byteBuffer.put((byte)1);
            byteBuffer.putInt(setCommand.getKey().length);
            byteBuffer.put(setCommand.getKey());
            byteBuffer.putInt(setCommand.getValue().length);
            byteBuffer.put(setCommand.getValue());
        }
        return byteBuffer.toString();
    }
}
