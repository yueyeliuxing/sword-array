package com.zq.sword.array.stream.io.object;

import com.zq.sword.array.stream.io.ResourceInputStream;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: 对象输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:42
 **/
public interface ObjectInputStream extends ResourceInputStream {

    /**
     * 读出数据
     * @return
     */
    Object readObject() throws IOException;

    /**
     * 读出数据
     * @param objs
     */
    void readObject(Object[] objs) throws IOException;
}
