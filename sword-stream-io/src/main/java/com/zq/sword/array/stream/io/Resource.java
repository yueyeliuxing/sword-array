package com.zq.sword.array.stream.io;

import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;

/**
 * @program: sword-array
 * @description: 资源存储
 * @author: zhouqi1
 * @create: 2019-01-14 10:20
 **/
public interface Resource {

    /**
     * 得到输入流
     * @return
     */
    ResourceInputStream openInputStream() throws InputStreamOpenException;

    /**
     * 得到输出流
     * @return
     */
    ResourceOutputStream openOutputStream() throws OutputStreamOpenException;

}
