package com.zq.sword.array.redis.protocol;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandEntity;

/**
 * @program: sword-array
 * @description: 协议解析
 * @author: zhouqi1
 * @create: 2018-07-03 15:33
 **/
public class ProtocolParser {

    public static final String DOLLAR_BYTE = "$";
    public static final String ASTERISK_BYTE = "*";
    public static final String PLUS_BYTE = "+";
    public static final String MINUS_BYTE = "-";
    public static final String COLON_BYTE = ":";
    public static final String SPLIT_BYTE = "\r\n";

    //*1
    //$4
    //PING
    public static RedisCommandEntity toRedisCommandEntity(String commandMsg) {
        RedisCommandEntity redisCommandEntity = new RedisCommandEntity();
        String[] params = commandMsg.split("\r\n");
        String[] args = new String[(params.length - 3)/2];
        int index = 0;
        for(int i = 0; i < params.length; i++){
            if(i == 0){
                continue;
            }
            if(i == 2){
                redisCommandEntity.setRedisCommand(RedisCommand.valueOf(params[2]));
            }else if(i % 2 == 0){
                args[index] = params[i];
                index++;

            }
        }
        redisCommandEntity.setArgs(args);
        return redisCommandEntity;
    }

    public static String toRedisCommandMsg(RedisCommandEntity redisCommandEntity){
        String[] args = redisCommandEntity.getArgs();
        int argLength = 0;
        if(args != null){
            argLength = args.length;
        }
        String commandName = redisCommandEntity.getRedisCommand().name();
        int commandLength = commandName.length();
        StringBuilder sb = new StringBuilder();
        sb.append(ASTERISK_BYTE)
                .append(argLength+1)
                .append(SPLIT_BYTE)
                .append(DOLLAR_BYTE)
                .append(commandLength)
                .append(SPLIT_BYTE)
                .append(commandName)
                .append(SPLIT_BYTE);

        if(argLength > 0){
            for(String arg : args) {
                sb.append(DOLLAR_BYTE)
                        .append(arg.length())
                        .append(SPLIT_BYTE)
                        .append(arg)
                        .append(SPLIT_BYTE);
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        RedisCommandEntity redisCommandEntity = toRedisCommandEntity("*1\r\n$4\r\nPING\r\n");
        System.out.println(redisCommandEntity);

        String redisCommandMsg = toRedisCommandMsg(redisCommandEntity);
        System.out.println(redisCommandMsg);
    }
}
