package com.zq.sword.array.mq.jade.bitcask;

import com.zq.sword.array.common.utils.DateUtil;

import java.util.Date;

/**
 * @program: sword-array
 * @description: 文件Id 生成器
 * @author: zhouqi1
 * @create: 2018-10-31 16:24
 **/
public class FileIdGenerater {

    public static class FileId {
        public String date;
        public Long dataId;

        public FileId(String date, Long dataId) {
            this.date = date;
            this.dataId = dataId;
        }

        public String toFileId(){
            return String.format("%s_%s", date, dataId);
        }


    }

    /**
     * 生成文件ID
     * @param date
     * @param dataId
     * @return
     */
    public static FileId getFileId(Date date, Long dataId){
        return new FileId(DateUtil.formatDate(date, DateUtil.YYYYMMDD), dataId);
    }

    public static FileId toFileId(String fileId){
        String[] a = fileId.split("_");
        return new FileId(a[0], Long.parseLong(a[1]));
    }
}
