package com.zq.sword.array.data.center;

import java.util.Collection;

/**
 * @program: sword-array
 * @description: 管理消息分片的容器
 * @author: zhouqi1
 * @create: 2019-01-16 10:27
 **/
public interface PartitionSystem {


    /**
     *  获取资源路径
     * @return
     */
    String storePath();

    /**
     * 指定分组是否存在
     * @param group
     * @return
     */
    boolean exist(String group);

    /**
     * 指定分组下的分片是否存在
     * @param group
     * @param partName
     * @return
     */
    boolean exist(String group, String partName);

    /**
     * 指定分片是否存在
     * @param partition
     * @return
     */
    boolean exist(Partition partition);

    /**
     * 得到指定ID的分片数据
     * @param group
     * @param partName
     * @return
     */
    Partition getPartition(String group, String partName);


    /**
     * 新创建一个分片 如果存在就抛出异常 创建失败
     * @param group
     * @param partName
     * @return
     */
    Partition createPartition(String group, String partName);

    /**
     * 把指定分片移动到其他分组
     * @param partition 分片
     * @param group 指定分组
     * @return  新的分片
     */
    Partition move(Partition partition, String group);

    /**
     * 把指定分片移动到其他分组
     * @param partition 分片
     * @param group 指定分组
     * @param name 指定名称
     * @return  新的分片
     */
    Partition move(Partition partition, String group, String name);

    /**
     * 把指定分片拷贝一份到其他分组
     * @param partition 分片
     * @param group 指定分组
     * @return  新的分片
     */
    Partition copy(Partition partition, String group);

    /**
     * 把指定分片拷贝一份到其他分组
     * @param partition 分片
     * @param group 指定分组
     * @param name 指定名称
     * @return  新的分片
     */
    Partition copy(Partition partition, String group, String name);

    /**
     * 删除指定组下的所有分片
     * @param group
     * @return
     */
    boolean remove(String group);

    /**
     * 删除指定分片
     * @param group
     * @param partName
     * @return
     */
    boolean remove(String group, String partName);

    /**
     * 删除指定分片
     * @param partition
     * @return
     */
    boolean remove(Partition partition);

    /**
     * 得到指定组的所有分片
     * @param group
     * @return
     */
    Collection<Partition> getPartitions(String group);

    /**
     * 得到所有的分片数据
     * @return
     */
    Collection<Partition> getPartitions();

    /**
     * 销毁
     */
    void destroy();


}
