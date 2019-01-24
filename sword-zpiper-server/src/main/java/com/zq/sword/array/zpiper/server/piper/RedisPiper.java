package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;
import com.zq.sword.array.mq.jade.embedded.AbstractEmbeddedBroker;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.handler.SimpleCycleDisposeHandler;
import com.zq.sword.array.redis.replicator.EmbeddedSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.writer.EmbeddedRedisWriter;
import com.zq.sword.array.redis.writer.RedisWriter;
import com.zq.sword.array.zpiper.server.piper.cluster.PiperCluster;
import com.zq.sword.array.zpiper.server.piper.cluster.ZkPiperCluster;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperStartState;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisPiper extends AbstractEmbeddedBroker implements Piper{

    private Logger logger = LoggerFactory.getLogger(RedisPiper.class);

    private NamePiper namePiper;

    private SlaveRedisReplicator redisReplicator;

    private RedisWriter redisWriter;

    private PiperCluster cluster;

    private volatile boolean isCanRegister = false;

    private volatile boolean isRegisterBroker = false;

    private CountDownLatch latch = new CountDownLatch(1);

    public RedisPiper(PiperConfig config) {
        super(config.piperId(), config.msgResourceLocation(), new ZkNameCoordinator(config.zkLocation()), config.piperLocation());
        this.namePiper = config.namePiper();
        //设置topic
        topics(namePiper.getGroup());

        CycleDisposeHandler<RedisCommand> cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //设置redis 复制器
        redisReplicator = new EmbeddedSlaveRedisReplicator(config.redisUri(), namePiper.getGroup(), this, cycleDisposeHandler);

        //设置redis 写入器
        redisWriter = new EmbeddedRedisWriter(config.redisConfig(), config.redisWriteTempFilePath(), this, cycleDisposeHandler);

        //创建集群协调器
        cluster = new ZkPiperCluster(config.zkLocation());
    }

    @Override
    public void start() {
        cluster.setStartState(namePiper, PiperStartState.STARTING);
        try{
            while(!cluster.register(namePiper, (dataEvent)->{
                HotspotEventType type = dataEvent.getType();
                if(HotspotEventType.PIPER_MASTER_NODE_DEL.equals(type)){
                    isCanRegister = true;
                    latch.countDown();
                }
            })){
                //作为从piper 先启动注册容器
                if(!isRegisterBroker){
                    super.start();
                    isRegisterBroker = true;
                }
                latch.await();
                if (isCanRegister) {
                    logger.info("监听到分配器临时节点消失，延迟5s启动");
                    Thread.sleep(5000);
                }
            }
        }catch (Exception e){
            logger.error("错误", e);
        }
        logger.info("抢占piper主节点成功：{}注册成功", id());

        //作为从piper 先启动注册容器
        if(!isRegisterBroker){
            super.start();
        }

        redisWriter.start();
        redisReplicator.start();

        cluster.setStartState(namePiper, PiperStartState.STARTED);

    }

    @Override
    public void shutdown() {
        redisReplicator.stop();
        super.shutdown();
        redisWriter.stop();

    }


}
