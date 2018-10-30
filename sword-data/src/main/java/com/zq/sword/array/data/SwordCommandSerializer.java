package com.zq.sword.array.data;

import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 命令序列化帮助类
 * @author: zhouqi1
 * @create: 2018-10-10 15:41
 **/
public class SwordCommandSerializer implements SwordSerializer<SwordCommand> {

    @Override
    public byte[] serialize(SwordCommand commandSword) {
        try {
            return JsonUtil.toJSONString(commandSword).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
