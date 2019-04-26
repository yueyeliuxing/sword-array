package com.zq.sword.array.data.storage;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据块
 * @author: zhouqi1
 * @create: 2019-01-16 10:30
 **/
public interface Partition {

    /**
     * 分片名称
     * @return
     */
    String name();

    /**
     * 分片所属的组
     * @return
     */
    String group();

    /**
     * 在指定偏移处写入数据
     * @param offset
     * @param entry
     * @return
     */
    long add(long offset, DataEntry entry);

    /**
     * 追加消息 末尾追加
     * @param entry
     * @return
     */
    long append(DataEntry entry);

    /**
     * 搜索指定偏移量的数据
     * @param offset
     * @return
     */
    DataEntry get(long offset);

    /**
     * 从指定偏移量顺序搜寻指定数量的消息
     * @param offset 指定偏移量
     * @param num 指定数量
     * @return 消息
     */
    List<DataEntry> orderGet(long offset, int num);

    /**
     * 复制一个分片
     * @param group
     * @param name
     * @return
     */
    Partition copy(String group, String name);

    /**
     * 销毁
     */
    void destroy();
}
