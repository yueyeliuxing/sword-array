package com.zq.sword.array.metadata.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 节点服务ID
 * @author: zhouqi1
 * @create: 2018-07-23 16:50
 **/
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class NodeId {

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

    public NodeId(NodeType type, String dc, String unitCategory, String unit, String group) {
        this.type = type;
        this.dc = dc;
        this.unitCategory = unitCategory;
        this.unit = unit;
        this.group = group;
    }
}
