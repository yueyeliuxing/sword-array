package com.zq.sword.array.common.service;

/**
 * @program: sword-array
 * @description: 生命周期接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:27
 **/
public abstract class AbstractLifecycle implements Lifecycle{

    private LifecycleState lifecycleState = new LifecycleState();

    @Override
    public void init() {
        lifecycleState.setLifecycleState(LifecycleState.LifecycleStateEnum.INIT);
    }

    @Override
    public void start() {
        lifecycleState.setLifecycleState(LifecycleState.LifecycleStateEnum.START);
    }

    @Override
    public void stop() {
        lifecycleState.setLifecycleState(LifecycleState.LifecycleStateEnum.STOP);
    }

    @Override
    public void destroy() {
        lifecycleState.setLifecycleState(LifecycleState.LifecycleStateEnum.DESTROY);
    }

    @Override
    public boolean isInit() {
        return lifecycleState.isInit();
    }

    @Override
    public boolean isStart() {
        return lifecycleState.isStart();
    }

    @Override
    public boolean isStop() {
        return lifecycleState.isStop();
    }

    @Override
    public boolean isDestroy() {
        return lifecycleState.isDestroy();
    }
}
