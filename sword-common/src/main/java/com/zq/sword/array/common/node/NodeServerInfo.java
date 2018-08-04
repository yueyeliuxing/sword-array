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
public class NodeServerInfo {

    private NodeServerId id;

    private String serverAddress;

    private int port;

    private int backupPort;

    public NodeServerInfo(String serverAddress, int port, int backupPort) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.backupPort = backupPort;
    }
}
