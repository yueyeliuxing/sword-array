package com.zq.sword.array.namer.piper;


import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: piper管理器
 * @author: zhouqi1
 * @create: 2019-06-10 10:04
 **/
public class MetaPiperSupervisor {

    private Map<String, NamePiper> namePipers;

    private Map<String, MetaPiperGroup> groups;

    private List<MetaPiperListener> listeners;

    public MetaPiperSupervisor() {
        namePipers = new HashMap<>();
        groups = new HashMap<>();
        listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * 注册监听器
     * @param listener
     */
    public void registerListener(MetaPiperListener listener){
        listeners.add(listener);
    }

    /**
     * 注册Piper
     * @param piper
     */
    public void registerPiper(NamePiper piper){
        synchronized (groups){
            MetaPiperGroup group = groups.get(piper.getGroup());
            if(group == null){
                group = new MetaPiperGroup(piper.getGroup());
            }
            MetaPiper metaPiper = new MetaPiper(piper.getLocation());
            group.addPiper(metaPiper);

            //触发监听器
            listeners.forEach(listener->listener.add(piper));

            namePipers.put(piper.getLocation(), piper);
        }


    }

    /**
     * 移除piper
     * @param piper
     */
    public void removePiper(NamePiper piper){
        synchronized (groups){
            MetaPiperGroup group = groups.get(piper.getGroup());
            if(group != null){
                MetaPiper metaPiper = group.getPiper(piper.getLocation());
                group.removePiper(metaPiper);

                //移除通知
                listeners.forEach(listener->listener.remove(piper));

            }

            namePipers.remove(piper.getLocation());
        }
    }

    /**
     * 得到指定的PiperGroup
     * @param groupName
     * @return
     */
    public MetaPiperGroup getPiperGroup(String groupName){
        return groups.get(groupName);
    }

    /**
     * 得到所有的组
     * @return
     */
    public Collection<MetaPiperGroup> getAllPiperGroup(){
        return Collections.unmodifiableCollection(groups.values());
    }

    /**
     * 得到所有的Piper元数据
     * @return
     */
    public Collection<NamePiper> allNamePipers(){
        return Collections.unmodifiableCollection(namePipers.values());
    }
}
