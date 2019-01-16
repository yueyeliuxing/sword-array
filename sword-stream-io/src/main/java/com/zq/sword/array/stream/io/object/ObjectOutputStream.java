package com.zq.sword.array.stream.io.object;

import java.io.IOException;
import java.util.List;

/**
 * @program: sword-array
 * @description: 对象输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:42
 **/
public interface ObjectOutputStream {

    /**
     * 写入数据
     * @return
     */
    void writeObject(Object obj) throws IOException;

    /**
     * 读出数据
     * @param objs
     */
    void writeObject(List<Object> objs) throws IOException;
}
