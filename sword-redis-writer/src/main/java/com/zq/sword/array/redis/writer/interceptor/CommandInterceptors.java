package com.zq.sword.array.redis.writer.interceptor;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.data.CommandMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 拦截去处理
 * @author: zhouqi1
 * @create: 2019-01-24 14:59
 **/
public class CommandInterceptors {

    private List<CommandInterceptor> interceptors;

    public CommandInterceptors() {
        this.interceptors = new ArrayList<>();
    }

    public void addInterceptor(CommandInterceptor interceptor){
        this.interceptors.add(interceptor);
    }

    /**
     * 命令写入之前进行拦截处理
     * @param command
     * @return 如果返回 null 命令被抛弃
     */
    public RedisCommand onWrite(RedisCommand command){
        RedisCommand redisCommand = command;
        if(interceptors != null && !interceptors.isEmpty()){
            for (CommandInterceptor interceptor : interceptors){
                if(redisCommand == null){
                    return redisCommand;
                }
                redisCommand = interceptor.onWrite(redisCommand);
            }
        }
        return redisCommand;
    }

    /**
     * 写入后的应答拦截
     * @param metadata
     */
    public  void onAcknowledgment(CommandMetadata metadata){
        if(interceptors != null && !interceptors.isEmpty()){
            for (CommandInterceptor interceptor : interceptors){
                interceptor.onAcknowledgment(metadata);
            }
        }
    }

}
