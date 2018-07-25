package com.zq.sword.array.data.lqueue.right.impl;

import com.zq.sword.array.data.lqueue.right.RightDataQueue;
import com.zq.sword.array.data.lqueue.item.DataItem;
import com.zq.sword.array.data.lqueue.item.DataItemId;

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
