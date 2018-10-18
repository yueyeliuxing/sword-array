package com.zq.sword.array.common.data;

import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordCommandDeserializer implements SwordDeserializer<SwordCommand> {

    @Override
    public SwordCommand deserialize(byte[] data) {
        try {
            return JsonUtil.parse(new String(data, "utf-8"), SwordCommand.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
