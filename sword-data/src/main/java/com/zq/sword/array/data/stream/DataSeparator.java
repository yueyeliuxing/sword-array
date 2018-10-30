package com.zq.sword.array.data.stream;

/**
 * @program: sword-array
 * @description: 数据分隔符
 * @author: zhouqi1
 * @create: 2018-10-30 11:44
 **/
public interface DataSeparator {

    /**
     * 返回分隔符
     * @return
     */
    String character();

    /**
     * 判断数据边界
     * @param data
     * @return  >-1 到边界
     */
    int isBoundary(byte[] data);

    /**
     * 获取单个数据
     * @param data
     * @return
     */
    byte[] toDataArray(byte[] data);
}
