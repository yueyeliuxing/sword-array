package com.zq.sword.array.stream.io;

/**
 * @program: sword-array
 * @description: 资源加载器
 * @author: zhouqi1
 * @create: 2019-01-16 15:44
 **/
public interface ResourceLoader {

    /**
     *  通过资源路径
     * @param resourceLocation
     * @return
     */
    Resource load(String resourceLocation);

    /**
     * 通过指定资源路径加载响应资源
     * @param resourceLocations
     * @return
     */
    Resource[] load(String[] resourceLocations);
}
