package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据块
 * @author: zhouqi1
 * @create: 2019-01-16 10:30
 **/
public interface Partition {

    /**
     * id
     * @return
     */
    long id();

    /**
     * 标签
     * @return
     */
    String tag();

    /**
     * 名称
     * @return
     */
    String name();

    /**
     * 路径
     * @return
     */
    String path();

    /**
     * topic 配置
     * @return
     */
    String topic();

    /**
     * 追加消息
     * @param message
     * @return
     */
    long append(Message message);

    /**
     * 搜索指定偏移量的消息
     * @param offset
     * @return
     */
    Message search(long offset);

    /**
     * 从指定偏移量顺序搜寻指定数量的消息
     * @param offset 指定偏移量
     * @param num 指定数量
     * @return 消息
     */
    List<Message> orderSearch(long offset, int num);

    /**
     * 关闭
     */
    void close();

    /**
     * 是否关闭
     * @return
     */
    boolean isClose();
}
