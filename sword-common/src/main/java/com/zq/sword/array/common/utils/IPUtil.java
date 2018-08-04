package com.zq.sword.array.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @program: sword-array
 * @description: IP工具类
 * @author: zhouqi1
 * @create: 2018-08-04 11:03
 **/
public class IPUtil {

    private static Logger logger = LoggerFactory.getLogger(IPUtil.class);

    /**
     * 获取本机IP地址
     * @return
     */
    public static String getServerIp(){
        try{
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress().toString(); //获取本机ip
        }catch (Exception e){
            logger.error("获取IP地址出错", e);
        }
        return null;
    }
}
