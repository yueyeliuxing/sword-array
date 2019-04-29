package com.zq.sword.array.network.rpc.protocol.dto.client;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-29 09:37
 **/
@Data
public class NameJob implements Serializable {
    private static final long serialVersionUID = -7468013121709051018L;

    /**
     *  任务名称 唯一
     */
    private String name;

    /**
     * 源redis key->piperGroup value->redisLocation
     */
    private Map<String, String> sourceRedis;
}
