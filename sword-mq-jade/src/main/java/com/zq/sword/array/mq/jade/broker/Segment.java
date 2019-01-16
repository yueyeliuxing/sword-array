package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.stream.io.Resource;

/**
 * @program: sword-array
 * @description: 数据段
 * @author: zhouqi1
 * @create: 2019-01-16 10:31
 **/
public interface Segment extends Resource {

    /**
     * 下一个段
     * @return
     */
    Segment next();

    /**
     * 设置下一个段
     * @param next
     */
    void next(Segment next);

    /**
     * id
     * @return
     */
    long id();

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
     * 数据长度
     * @return
     */
    long length();

    /**
     * 是否达到最大长度
     * @return
     */
    boolean isFull();

    /**
     * 最后修改时间
     * @return
     */
    long lastModifyTime();
}
