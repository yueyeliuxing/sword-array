package com.zq.sword.array.network.rpc.protocol.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 节点信息
 * @author: zhouqi1
 * @create: 2018-10-22 20:47
 **/
@Data
@ToString
@NoArgsConstructor
public class NamePiper implements Serializable {

    /**
     * id
     */
    private long id;

    /**
     * piper 组名称
     */
    private String group;

    /**
     * ip:port
     */
    private String location;

    public NamePiper(long id, String group, String location) {
        this.id = id;
        this.group = group;
        this.location = location;
    }
}
