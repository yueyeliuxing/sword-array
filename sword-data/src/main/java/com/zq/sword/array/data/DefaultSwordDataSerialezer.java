package com.zq.sword.array.data;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class DefaultSwordDataSerialezer implements SwordDataSerializer<DefaultSwordData> {

    @Override
    public byte[] serialize(DefaultSwordData defaultSwordData) {
        return new byte[0];
    }
}
