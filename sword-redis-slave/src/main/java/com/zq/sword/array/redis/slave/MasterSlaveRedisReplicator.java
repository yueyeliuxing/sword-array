package com.zq.sword.array.redis.slave;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
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
public class MasterSlaveRedisReplicator implements SlaveRedisReplicator<SwordCommand> {

    private Logger logger = LoggerFactory.getLogger(MasterSlaveRedisReplicator.class);

    private Replicator replicator;

    public MasterSlaveRedisReplicator(String uri) {
        try {
            replicator = new RedisReplicator(uri);
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addEventListener(DataEventListener<SwordCommand> listener) {
        return replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if(event instanceof Command){
                    Command command = (Command) event;
                    DataEvent<SwordCommand> dataEvent = new DataEvent<>();
                    dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                    dataEvent.setData(CommandParser.parse(command));
                    listener.listen(dataEvent);
                }
            }
        });
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
    public void reset() {
        try {
            replicator.close();
            replicator.open();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }
}
