package com.zq.sword.array.metadata;

import com.zq.sword.array.metadata.data.SwordConfig;

/**
 * @program: sword-array
 * @description: 配置管理器
 * @author: zhouqi1
 * @create: 2018-10-22 20:01
 **/
public interface ConfigManager {

    /**
     * 获取配置信息
     * @return
     */
    SwordConfig config();
}
