package com.zq.sword.array.metadata.data;

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
public class NodeNamingInfo {

    /**
     * 主机 ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 备份服务端口
     */
    private Integer backupPort;

    public NodeNamingInfo(String host, Integer port, Integer backupPort) {
        this.host = host;
        this.port = port;
        this.backupPort = backupPort;
    }
}
