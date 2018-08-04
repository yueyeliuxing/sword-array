package com.zq.sword.array.common.node;

/**
 * @program: sword-array
 * @description: NodeServer配置key
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class NodeServerConfigKey {

    /**
     * zk连接地址
     */
    public static final String ZK_CONNECT_ADDR = "zk.connect.addr";

    /**
     * zk连接超时时间
     */
    public static final String ZK_CONNECT_TIMEOUT = "zk.connect.timeout";

    /**
     * Redis连接地址
     */
    public static final String REDIS_CONNECT_ADDR = "redis.connect.addr";

    /**
     * T-Right队列数据索引文件存储地址
     */
    public static final String T_RIGHT_DATA_INDEX_FILE_PATH = "t-right.data.index.file.path";

    /**
     * T-Right队列数据文件存储地址
     */
    public static final String T_RIGHT_DATA_ITEM_FILE_PATH = "t-right.data.item.file.path";

    /**
     * T-Right队列索引数据持久化策略--内存中添加的数目
     */
    public static final String T_RIGHT_DATA_INDEX_PERSISTENCE_NUM = "t-right.data.index.persistence.num";

    /**
     * T-Right队列索引数据持久化策略--空闲时间
     */
    public static final String T_RIGHT_DATA_INDEX_PERSISTENCE_FREE_TIME = "t-right.data.index.persistence.free.time";

    /**
     * T-Left队列数据文件存储地址
     */
    public static final String T_LEFT_DATA_ITEM_FILE_PATH = "t-left.data.item.file.path";

    /**
     * 节点数据传输服务启动的端口
     */
    public static final String NODE_SERVER_START_PORT = "node.server.start.port";

    /**
     * 节点数据备份传输服务启动的端口
     */
    public static final String NODE_SERVER_BACKUP_START_PORT = "node.server.backup.start.port";
}
