package com.zq.sword.array.data.rqueue.helper;

import com.zq.sword.array.data.rqueue.domain.DataIndex;

import java.nio.*;

/**
 * @program: sword-array
 * @description: 数据索引帮助类
 * @author: zhouqi1
 * @create: 2018-07-26 11:11
 **/
public class DataIndexHelper {

    /**
     * 从数据行转换为数据索引类
     * @param dataLine
     * @return
     */
    public static DataIndex getDataIndex(String dataLine){
        DataIndex dataIndex = new DataIndex();
        ByteBuffer byteBuffer = ByteBuffer.wrap(dataLine.getBytes());
        dataIndex.setDataId(byteBuffer.getLong());
        int len = byteBuffer.getInt();
        byte[] fileIdBytes = new byte[len];
        byteBuffer.get(fileIdBytes);
        dataIndex.setFileId(new String(fileIdBytes));
        dataIndex.setValueLength(byteBuffer.getLong());
        dataIndex.setValuePosition(byteBuffer.getLong());
        dataIndex.setTimestamp(byteBuffer.getLong());
        return dataIndex;
    }

    /**
     * 从数据索引类转换为数据行
     * @param dataIndex
     * @return
     */
    public static String getDataLine(DataIndex dataIndex){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putLong(dataIndex.getDataId());
        String fileId = dataIndex.getFileId();
        byteBuffer.putInt(fileId.length());
        byteBuffer.put(fileId.getBytes());
        byteBuffer.putLong(dataIndex.getValueLength());
        byteBuffer.putLong(dataIndex.getValuePosition());
        byteBuffer.putLong(dataIndex.getTimestamp());
        return byteBuffer.toString();
    }
}
