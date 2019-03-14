package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;
import com.zq.sword.array.mq.jade.broker.AbstractEmbeddedBroker;
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
public abstract class AbstractPiper extends AbstractEmbeddedBroker implements Piper{

    private Logger logger = LoggerFactory.getLogger(AbstractPiper.class);

    protected NamePiper namePiper;

    protected PiperCluster cluster;

    private volatile boolean isCanRegister = false;

    private volatile boolean isRegisterBroker = false;

    private CountDownLatch latch = new CountDownLatch(1);

    public AbstractPiper(PiperConfig config) {
        super(config.piperId(), config.msgResourceLocation(), new ZkNameCoordinator(config.zkLocation()), config.piperLocation());

        //创建集群协调器
        cluster = new ZkPiperCluster(config.zkLocation());

        this.namePiper = config.namePiper();
        //设置topic
        topics(namePiper.getGroup());

        tag(namePiper.getType().name().toLowerCase());

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
        doStartModule();
        cluster.setStartState(namePiper, PiperStartState.STARTED);

    }

    protected abstract void doStartModule();


    @Override
    public void shutdown() {
        super.shutdown();
        this.cluster.close();
        doStopModule();
    }

    protected abstract void doStopModule();


}
