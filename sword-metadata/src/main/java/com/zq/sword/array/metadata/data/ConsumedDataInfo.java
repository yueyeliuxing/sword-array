package com.zq.sword.array.metadata.data;

import com.zq.sword.array.common.node.NodeServerId;
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
public class ConsumedDataInfo {

    private Long dataId;

    public ConsumedDataInfo(Long dataId) {
        this.dataId = dataId;
    }
}
