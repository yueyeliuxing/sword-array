package com.zq.sword.array.config.client;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkConfigPathBuilder {

    public static final String ZK_ROOT = "/p-piper";

    public static final String ZK_SWORD_PIPER = "/piper";

    public static final String ZK_SWORD_PIPER_CONFIG = "/config";

    public static String getRealPath(String path) {
        return ZK_ROOT+path;
    }


    /**
     * 转换得到piper服务的启动配置项
     * @param id
     * @return
     */
    public static String buildNodeConfigPath(ApplicationId id) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s", id.getDc(),id.getUnitCategory(),
                id.getUnit(), ZK_SWORD_PIPER, id.getGroup(), ZK_SWORD_PIPER_CONFIG));
    }

}
