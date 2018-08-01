package com.zq.sword.array.data.lqueue.helper;


import com.zq.sword.array.data.lqueue.domain.DataItem;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 数据索引帮助类
 * @author: zhouqi1
 * @create: 2018-07-26 11:11
 **/
public class DataItemHelper {

    /**
     * 从数据行转换为数据项类
     * @param dataLine
     * @return
     */
    public static DataItem getDataItem(String dataLine){
        DataItem dataItem = new DataItem();
        ByteBuffer byteBuffer = ByteBuffer.wrap(dataLine.getBytes());
        dataItem.setId(byteBuffer.getLong());
        int len = byteBuffer.getInt();
        byte[] valueBytes = new byte[len];
        byteBuffer.get(valueBytes);
        dataItem.setValue(new String(valueBytes));
        dataItem.setTimestamp(byteBuffer.getLong());
        int crcLen = byteBuffer.getInt();
        byte[] crcBytes = new byte[crcLen];
        byteBuffer.get(crcBytes);
        dataItem.setCrc(new String(crcBytes));
        return dataItem;
    }

    /**
     * 从数据项类转换为数据行
     * @param dataItem
     * @return
     */
    public static String getDataLine(DataItem dataItem){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putLong(dataItem.getId());
        String value = dataItem.getValue();
        byteBuffer.putInt(value.length());
        byteBuffer.put(value.getBytes());
        byteBuffer.putLong(dataItem.getTimestamp());
        String crc = dataItem.getCrc();
        byteBuffer.putInt(crc.length());
        byteBuffer.put(crc.getBytes());
        return byteBuffer.toString();
    }
}
