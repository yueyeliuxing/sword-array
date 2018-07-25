package com.zq.sword.array.redis.rdb.config;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: sword-array
 * @description: RDB数据结构
 * @author: zhouqi1
 * @create: 2018-07-03 13:59
 **/
@Data
@ToString
public class RDBConfig {

    private String headflag;

    private String version;

    private List<DBConfig> dbConfigs;

    private String footFlag;

    private Long checkSum;

}
