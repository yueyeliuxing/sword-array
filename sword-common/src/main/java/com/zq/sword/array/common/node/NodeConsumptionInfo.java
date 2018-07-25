package com.zq.sword.array.common.node;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 节点服务ID
 * @author: zhouqi1
 * @create: 2018-07-23 16:50
 **/
@Data
@ToString
public class NodeConsumptionInfo {

    private NodeServerId id;

    private String consumeUnitName;

    private String dataItemId;
}
