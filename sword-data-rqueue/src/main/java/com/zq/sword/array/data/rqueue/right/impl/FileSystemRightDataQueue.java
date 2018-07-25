package com.zq.sword.array.data.rqueue.right.impl;

import com.zq.sword.array.data.rqueue.item.DataItem;
import com.zq.sword.array.data.rqueue.item.DataItemId;
import com.zq.sword.array.data.rqueue.right.RightDataQueue;

import java.util.List;

/**
 * @program: sword-array
 * @description: 文件系统Right队列存储
 * @author: zhouqi1
 * @create: 2018-07-23 17:52
 **/
public class FileSystemRightDataQueue implements RightDataQueue {

    @Override
    public void push(DataItem dataItem) {

    }

    @Override
    public DataItem poll() {
        return null;
    }

    @Override
    public List<DataItem> pollAfterId(DataItemId id) {
        return null;
    }

    @Override
    public List<DataItem> pollAfterId(DataItemId id, int maxNum) {
        return null;
    }
}
