package com.zq.sword.array.redis.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: redis 命令实体
 * @author: zhouqi1
 * @create: 2018-07-03 15:39
 **/
@Data
@ToString
@NoArgsConstructor
public class RedisCommandEntity {

     private RedisCommand redisCommand;

     private String[] args;

     public RedisCommandEntity(RedisCommand redisCommand) {
          this.redisCommand = redisCommand;
     }

     public RedisCommandEntity(RedisCommand redisCommand, String[] args) {
          this.redisCommand = redisCommand;
          this.args = args;
     }
}
