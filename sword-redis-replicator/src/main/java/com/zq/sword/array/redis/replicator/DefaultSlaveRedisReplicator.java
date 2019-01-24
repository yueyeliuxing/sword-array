package com.zq.sword.array.redis.replicator;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.redis.command.CommandMetadata;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptors;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListeners;
import com.zq.sword.array.redis.replicator.util.RedisCommandBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class DefaultSlaveRedisReplicator implements SlaveRedisReplicator {

    private Logger logger = LoggerFactory.getLogger(DefaultSlaveRedisReplicator.class);

    private CommandInterceptors commandInterceptors;

    private RedisReplicatorListeners redisReplicatorListeners;

    private Replicator replicator;

    public DefaultSlaveRedisReplicator(String uri) {
        this.redisReplicatorListeners = new RedisReplicatorListeners();
        this.commandInterceptors = new CommandInterceptors();
        try {
            replicator = new RedisReplicator(uri);
            replicator.addEventListener(new RedisReplicatorEventListener());
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
            replicator.open();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            replicator.close();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addCommandInterceptor(CommandInterceptor interceptor) {
        commandInterceptors.addCommandInterceptor(interceptor);
    }

    @Override
    public void addRedisReplicatorListener(RedisReplicatorListener redisReplicatorListener) {
        redisReplicatorListeners.addRedisReplicatorListener(redisReplicatorListener);
    }

    /**
     * redis 复制时间
     */
    private class RedisReplicatorEventListener implements EventListener {

        @Override
        public void onEvent(Replicator replicator, Event event) {
            if(event instanceof Command){
                Command command = (Command) event;
                RedisCommand redisCommand = RedisCommandBuilder.buildSwordCommand(command);
                if((redisCommand = commandInterceptors.interceptor(redisCommand)) == null){
                    return;
                }
                try{
                    redisReplicatorListeners.receive(redisCommand);
                    commandInterceptors.onAcknowledgment(new CommandMetadata(redisCommand));
                }catch (Exception e){
                    commandInterceptors.onAcknowledgment(new CommandMetadata(redisCommand, e));
                    logger.error("接收命令出错", e);
                }

            }
        }
    }
}
