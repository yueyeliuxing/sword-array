package com.zq.sword.array.namer.piper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 元Piper组
 * @author: zhouqi1
 * @create: 2019-06-10 09:53
 **/
public class MetaPiperGroup {

    /**
     * 组名称
     */
    private String name;

    private Map<String, MetaPiper> metaPipers;

    public MetaPiperGroup(String name) {
        this.name = name;
        metaPipers = new ConcurrentHashMap<>();
    }

    /**
     * 获得组名称
     * @return
     */
    public String name(){
        return name;
    }

    /**
     * 添加piper
     * @param piper
     */
    public void addPiper(MetaPiper piper){
        metaPipers.put(piper.location(), piper);
    }

    /**
     * 移除piper
     * @param piper
     */
    public void removePiper(MetaPiper piper){
        metaPipers.remove(piper.location());
    }

    /**
     * 得到piper
     * @param location
     * @return
     */
    public MetaPiper getPiper(String location){
        return metaPipers.get(location);
    }

    /**
     * 得到所有的piper
     * @return
     */
    public Collection<MetaPiper> allPipers(){
        return Collections.unmodifiableCollection(metaPipers.values());
    }

}
