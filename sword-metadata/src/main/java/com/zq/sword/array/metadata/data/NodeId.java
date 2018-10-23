package com.zq.sword.array.metadata.data;

import com.zq.sword.array.common.node.NodeServerRole;
import com.zq.sword.array.common.node.NodeServerType;
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
    private String dcName;

    /**
     * 单元类别名称
     */
    private String unitCategoryName;

    /**
     * 单元名称
     */
    private String unitName;

    /**
     * 服务器名称
     */
    private String serverName;

    public NodeId(NodeType type, String dcName, String unitCategoryName, String unitName, String serverName) {
        this.type = type;
        this.dcName = dcName;
        this.unitCategoryName = unitCategoryName;
        this.unitName = unitName;
        this.serverName = serverName;
    }
}
