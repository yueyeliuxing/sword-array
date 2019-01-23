package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.mq.jade.broker.AbstractApplicationBroker;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;
import com.zq.sword.array.zpiper.server.cluster.data.PiperType;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class ApplicationPiper extends AbstractApplicationBroker implements Piper{

    /**
     * id
     */
    private long id;

    /**
     * 类型
     */
    private PiperType type;

    /**
     * 机房名称
     */
    private String dc;

    /**
     * 单元类别名称
     */
    private String unitCategory;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * piper 组名称
     */
    private String group;

    /**
     * ip:port
     */
    private String location;

    public ApplicationPiper(PiperConfig config) {
        super(config.piperId(), config.msgResourceLocation(), new ZkNameCoordinator(config.zkLocation()), config.piperLocation());

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean started() {
        return false;
    }
}
