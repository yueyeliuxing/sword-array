package com.zq.sword.array.conf.listener;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 数据改变事件
 * @author: zhouqi1
 * @create: 2018-07-23 17:24
 **/
@Data
@ToString
public class DataEvent<T> {

    private DataEventType type;

    private T data;

}
