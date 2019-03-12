package com.zq.sword.array.mq.jade.coordinator.data;

import java.util.*;

/**
 * @program: sword-array
 * @description: 消费清单信息
 * @author: zhouqi1
 * @create: 2019-01-21 16:53
 **/
public class ConsumeDetailedInfo {

    private Map<Long, List<String>> detailed;

    public ConsumeDetailedInfo() {
        detailed = new HashMap<>();
    }

    public ConsumeDetailedInfo(String data) {
        detailed = new HashMap<>();
        if(data != null && !"".equals(data)){
            String[] detailedItems = data.split(";");
            for (String detailedItem : detailedItems){
                String[] cIds = detailedItem.split(":");
                String[] partIds = cIds[1].split(",");
                List<String> pIds = new ArrayList<>();
                Collections.addAll(pIds, partIds);
                detailed.put(Long.parseLong(cIds[0]), pIds);
            }
        }
    }

    public List<String> getPartIds(Long consumerId){
        return detailed.get(consumerId);
    }

    /**
     * 添加清单项
     * @param cId
     * @param partId
     */
    public void addDetailedItem(Long cId, String partId){
        List<String> partIds = detailed.get(cId);
        if(partIds == null){
            partIds = new ArrayList<>();
            detailed.put(cId, partIds);
        }
        partIds.add(partId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(detailed != null && !detailed.isEmpty()){
            for(Long cId : detailed.keySet()){
                builder.append(cId).append(":");
                List<String> partIds = detailed.get(cId);
                for(String partId : partIds){
                    builder.append(partId).append(",");
                }
                builder.deleteCharAt(builder.length()-1);
                builder.append(";");
            }
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }
}
