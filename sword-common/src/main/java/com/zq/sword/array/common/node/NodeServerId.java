package com.zq.sword.array.common.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 节点服务ID
 * @author: zhouqi1
 * @create: 2018-07-23 16:50
 **/
@Data
@ToString
@EqualsAndHashCode
public class NodeServerId {

    private String dcName;

    private String unitName;

    private String serverName;
}
