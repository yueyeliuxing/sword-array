package com.zq.sword.array.zpiper.server.cluster.data;

import com.zq.sword.array.metadata.data.NodeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 节点信息
 * @author: zhouqi1
 * @create: 2018-10-22 20:47
 **/
@Data
@ToString
@NoArgsConstructor
public class NamePiper {

    /**
     * id
     */
    private long id;

    /**
     * 类型
     */
    private NodeType type;

    /**
     * 机房名称
     */
    private String dc;

    /**
     * 单元类别名称
     */
    private String unitCategory;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * piper 组名称
     */
    private String group;

    /**
     * ip:port
     */
    private String location;

    public NamePiper(long id, NodeType type, String dc, String unitCategory, String unit, String group, String location) {
        this.id = id;
        this.type = type;
        this.dc = dc;
        this.unitCategory = unitCategory;
        this.unit = unit;
        this.group = group;
        this.location = location;
    }
}
