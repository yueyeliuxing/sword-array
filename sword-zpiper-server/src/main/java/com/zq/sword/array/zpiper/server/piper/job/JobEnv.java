package com.zq.sword.array.zpiper.server.piper.job;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: Job环境
 * @author: zhouqi1
 * @create: 2019-04-26 14:36
 **/
public class JobEnv {

    /**
     *  任务名称 唯一
     */
    private String name;

    /**
     * 源Group
     */
    private String piperGroup;

    /**
     * 源redis
     */
    private String sourceRedis;

    /**
     * 数据需复制到的其他Piper
     */
    private List<String> replicatePipers;

    /**
     * 目标piper  PiperGroup|PiperLocation
     * 从目标piper获取数据
     */
    private List<String> targetPipers;

    /**
     * 复制piper变动的监听器
     */
    private List<PiperChangeListener> replicatePiperChangeListeners;

    /**
     * 目标piper变动的监听器
     */
    private List<PiperChangeListener> targetPiperChangeListeners;

    public JobEnv(String name, String piperGroup, String sourceRedis, List<String> replicatePipers, List<String> targetPipers) {
        this.name = name;
        this.piperGroup = piperGroup;
        this.sourceRedis = sourceRedis;
        this.replicatePipers = replicatePipers;
        this.targetPipers = targetPipers;
        this.replicatePiperChangeListeners = new CopyOnWriteArrayList<>();
        this.targetPiperChangeListeners = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPiperGroup() {
        return piperGroup;
    }

    public String getSourceRedis() {
        return sourceRedis;
    }

    public List<String> getReplicatePipers(PiperChangeListener piperChangeListener) {
        replicatePiperChangeListeners.add(piperChangeListener);
        return replicatePipers;
    }

    public List<String> getTargetPipers(PiperChangeListener piperChangeListener) {
        targetPiperChangeListeners.add(piperChangeListener);
        return targetPipers;
    }

    /**
     * 发射复制piper改变时间
     * @param incrementPipers
     * @param decreasePipers
     */
    public void emitterReplicatePiperChangeEvent(List<String> incrementPipers, List<String> decreasePipers){
        if(replicatePiperChangeListeners != null && !replicatePiperChangeListeners.isEmpty()){
            for(PiperChangeListener piperChangeListener : replicatePiperChangeListeners){
                if(incrementPipers != null && !incrementPipers.isEmpty()){
                    piperChangeListener.increment(incrementPipers);
                }
                if(decreasePipers != null && !decreasePipers.isEmpty()){
                    piperChangeListener.decrease(decreasePipers);
                }
            }
        }
    }

    /**
     * 发射目标piper改变时间
     * @param incrementPipers
     * @param decreasePipers
     */
    public void emitterTargetPiperChangeEvent(List<String> incrementPipers, List<String> decreasePipers){
        if(targetPiperChangeListeners != null && !targetPiperChangeListeners.isEmpty()){
            for(PiperChangeListener piperChangeListener : targetPiperChangeListeners){
                if(incrementPipers != null && !incrementPipers.isEmpty()){
                    piperChangeListener.increment(incrementPipers);
                }
                if(decreasePipers != null && !decreasePipers.isEmpty()){
                    piperChangeListener.decrease(decreasePipers);
                }
            }
        }
    }
}
