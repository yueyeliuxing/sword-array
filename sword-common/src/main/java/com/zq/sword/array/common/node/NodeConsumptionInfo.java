package com.zq.sword.array.common.node;

import lombok.Data;
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
public class NodeConsumptionInfo {

    private NodeServerId id;

    private Long dataItemId;

    public NodeConsumptionInfo(NodeServerId id, Long dataItemId) {
        this.id = id;
        this.dataItemId = dataItemId;
    }
}
