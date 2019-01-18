package com.zq.sword.array.common.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 数据改变事件
 * @author: zhouqi1
 * @create: 2018-07-23 17:24
 **/
@Data
@ToString
@NoArgsConstructor
public class HotspotEvent<T> {

    private HotspotEventType type;

    private T data;

    public HotspotEvent(HotspotEventType type, T data) {
        this.type = type;
        this.data = data;
    }
}
