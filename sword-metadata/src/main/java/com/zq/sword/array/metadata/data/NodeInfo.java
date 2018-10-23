package com.zq.sword.array.metadata.data;

import com.zq.sword.array.common.node.NodeServerRole;
import com.zq.sword.array.common.node.NodeServerType;
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
public class NodeInfo {

    private NodeId id;



    /**
     * 角色
     */
    private NodeRole role;

    public NodeInfo(NodeId id) {
        this.id = id;
    }
}
