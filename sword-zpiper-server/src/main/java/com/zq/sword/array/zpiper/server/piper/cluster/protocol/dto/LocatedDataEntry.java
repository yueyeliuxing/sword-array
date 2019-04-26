package com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto;

import com.zq.sword.array.data.storage.DataEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 已经定位的消息
 * @author: zhouqi1
 * @create: 2019-01-18 09:47
 **/
@Data
@ToString
@NoArgsConstructor
public class LocatedDataEntry implements Serializable {
    private static final long serialVersionUID = -2603635956907360685L;

    /**
     * 分片组
     */
    private String partGroup;

    /**
     * 分片名称
     */
    private String partName;

    /**
     * 实际的消息
     */
    private DataEntry entry;

    public LocatedDataEntry(String partGroup, String partName, DataEntry entry) {
        this.partGroup = partGroup;
        this.partName = partName;
        this.entry = entry;
    }
}
