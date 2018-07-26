package com.zq.sword.array.common.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: sword-array
 * @description: 日期工具包
 * @author: zhouqi1
 * @create: 2018-07-26 10:23
 **/
public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDD = "yyyyMMdd";

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrentDate(){
        return formatDate(new Date(), YYYY_MM_DD);
    }

    /**
     * 格式化时间
     * @param date
     * @param format
     * @return
     */
    public static Date parseDate(String date, String format){
       try{
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
           return  simpleDateFormat.parse(date);
       }catch (Exception e){
           logger.error(" simpleDateFormat.parse", e);
           return null;
       }
    }

    /**
     * 格式化时间
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }
}
