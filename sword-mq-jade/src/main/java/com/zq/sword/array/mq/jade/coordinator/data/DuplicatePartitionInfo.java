package com.zq.sword.array.mq.jade.coordinator.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: sword-array
 * @description: 分片注册数据
 * @author: zhouqi1
 * @create: 2019-01-21 14:34
 **/
@Data
@ToString
@NoArgsConstructor
public class DuplicatePartitionInfo {

    /**
     * 主的location
     */
    private String master;

    /**
     * 备的location
     */
    private Set<String> slaves;

    public DuplicatePartitionInfo(String master) {
        this.master = master;
        this.slaves = new HashSet<>();
    }

    public void addSlave(String slave){
        slaves.add(slave);
    }

    /**
     * 序列化
     * @return
     */
    public byte[] serialize() {
        int masterLen = 0;
        if(!"".equals(master)){
            masterLen = master.length();
        }

        int slaveLen = 0;
        int slaveValueLen = 0;
        if(slaves != null && !slaves.isEmpty()){
            slaveLen = slaves.size();
            for(String slave : slaves){
                slaveValueLen += 4 + slave.length();
            }
        }

        int capacity = 4 + master.length() + 4 + slaveValueLen;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.putInt(masterLen);
        if(masterLen > 0){
            byteBuffer.put(master.getBytes());
        }
        byteBuffer.putInt(slaveLen);
        if(slaveLen > 0){
            for(String slave : slaves){
                byteBuffer.putInt(slave.length());
                byteBuffer.put(slave.getBytes());
            }
        }
        return byteBuffer.array();
    }


    /**
     * 反序列化
     * @param data
     */
    public void deserialize(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int masterLen = byteBuffer.getInt();
        if(masterLen ==  0){
            return;
        }
        byte[] masterBytes = new byte[masterLen];
        byteBuffer.get(masterBytes);
        master = new String(masterBytes);

        int slaveLen = byteBuffer.getInt();
        if(slaveLen == 0){
            return;
        }
        slaves = new HashSet<>();
        for(int i = 0; i < slaveLen; i++){
            int sLen = byteBuffer.getInt();
            byte[] sValueBytes = new byte[sLen];
            byteBuffer.get(sValueBytes);
            slaves.add(new String(sValueBytes));
        }
    }

}
