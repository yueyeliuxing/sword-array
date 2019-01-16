package com.zq.sword.array.stream.io.object;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: 对象输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:42
 **/
public interface ObjectInputStream {

    /**
     * 读出数据
     * @return
     */
    Object readObject() throws IOException;

    /**
     * 读出数据
     * @param objs
     */
    void read(Object[] objs) throws IOException;
}
