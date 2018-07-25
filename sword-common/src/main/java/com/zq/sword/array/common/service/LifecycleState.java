package com.zq.sword.array.common.service;

/**
 * @program: sword-array
 * @description: 生命周期接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:27
 **/
public class LifecycleState {

    private LifecycleStateEnum lifecycleStateEnum = LifecycleStateEnum.NO_INIT;

    public enum LifecycleStateEnum {

        NO_INIT((byte)0, "未初始化"),
        INIT((byte)1, "初始化"),
        START((byte)2, "开启"),
        STOP((byte)3, "停止"),
        DESTROY((byte)4, "销毁"),
        ;

        private byte state;

        private String desc;

        LifecycleStateEnum(byte state, String desc) {
            this.state = state;
            this.desc = desc;
        }
    }

    public void setLifecycleState(LifecycleStateEnum lifecycleState) {
        lifecycleStateEnum = lifecycleState;
    }

    public boolean isInit() {
        return lifecycleStateEnum == LifecycleStateEnum.INIT;
    }

    public boolean isStart() {
        return lifecycleStateEnum == LifecycleStateEnum.START;
    }

    public boolean isStop() {
        return lifecycleStateEnum == LifecycleStateEnum.STOP;
    }

    public boolean isDestroy() {
        return lifecycleStateEnum == LifecycleStateEnum.DESTROY;
    }


}
