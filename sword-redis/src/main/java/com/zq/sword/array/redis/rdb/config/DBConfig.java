package com.zq.sword.array.redis.rdb.config;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: DB数据结构
 * @author: zhouqi1
 * @create: 2018-07-03 14:02
 **/
@Data
@ToString
public class DBConfig {

    private String flag;

    private Integer dbNumber;
}
