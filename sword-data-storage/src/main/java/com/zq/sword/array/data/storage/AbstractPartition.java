package com.zq.sword.array.data.storage;

/**
 * @program: sword-array
 * @description: 抽象分片
 * @author: zhouqi1
 * @create: 2019-04-24 15:21
 **/
public abstract class AbstractPartition implements Partition {

    private String name;

    private String group;

    public AbstractPartition(String group, String name) {
        this.group = group;
        this.name = name;
    }

    @Override
   public String name(){
        return name;
   }

    @Override
    public String group() {
        return group;
    }
}
