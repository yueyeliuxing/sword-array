package com.zq.sword.array.namer.piper;

import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;

import java.util.*;

/**
 * @program: sword-array
 * @description: Piper监控系统
 * @author: zhouqi1
 * @create: 2019-04-28 17:41
 **/
public class NamePiperSystem {

     /**
     * key->PiperGroup value -> piper
     */
    private Map<String, List<NamePiper>> groupPipers;

    /**
     * key->piperId value -> piper
     */
    private Map<Long, NamePiper> idPipers;

    public NamePiperSystem(){
        groupPipers = new HashMap<>();
        idPipers = new HashMap<>();
    }

    /**
     * 注册piper
     * @param piper
     */
    public synchronized void addPiper(NamePiper piper){
        Long piperId = piper.getId();
        String piperGroup = piper.getGroup();
        List<NamePiper> pipers = groupPipers.get(piperGroup);
        if(pipers == null){
            pipers = new ArrayList<>();
            groupPipers.put(piperGroup, pipers);
        }
        pipers.add(piper);

        idPipers.put(piperId, piper);

    }

    /**
     * 获取所有的pipers
     * @return
     */
    public Collection<NamePiper> allPipers(){
        return Collections.unmodifiableCollection(idPipers.values());
    }

    /**
     * 获取指定Id的piper
     * @param piperId
     * @return
     */
    public NamePiper getPiper(Long piperId){
        return idPipers.get(piperId);
    }

    /**
     * 移除指定id的piper
     * @param piperId
     */
    public synchronized void removePiper(Long piperId){
        NamePiper piper = idPipers.get(piperId);
        idPipers.remove(piperId);

        List<NamePiper> pipers =  groupPipers.get(piper.getGroup());
        if(pipers != null){
            pipers.remove(piper);
        }
    }


    /**
     * 得到指定group上的所有piper
     * @param piperGroup
     * @return
     */
    public List<NamePiper> getGroupPipers(String piperGroup) {
        return groupPipers.get(piperGroup);
    }


}
