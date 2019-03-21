package com.zq.sword.array.zpiper.server.piper.cluster.util;

import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkClusterNodePathBuilder {

    public static final String ZK_ROOT = "/p-piper";

    public static final String ZK_SWORD_PIPER = "/piper";

    public static final String ZK_SWORD_PIPER_MASTER = "/master";

    public static final String ZK_SWORD_PIPER_MASTER_RUNNING = "/running";

    public static final String ZK_SWORD_PIPER_MASTER_STATER_STATE = "/stater-state";

    public static String getRealPath(String path) {
        return ZK_ROOT+path;
    }

    /**
     * 转换得到piper服务的Master
     * @param piper
     * @return
     */
    public static String buildPiperMasterPath(NamePiper piper) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s", piper.getDc(),piper.getUnitCategory(),
                piper.getUnit(), ZK_SWORD_PIPER, piper.getGroup(), ZK_SWORD_PIPER_MASTER));
    }

    /**
     * 转换得到piper服务的Master
     * @param piper
     * @return
     */
    public static String buildPiperRunningPath(NamePiper piper) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s%s", piper.getDc(),piper.getUnitCategory(),
                piper.getUnit(), ZK_SWORD_PIPER, piper.getGroup(), ZK_SWORD_PIPER_MASTER, ZK_SWORD_PIPER_MASTER_RUNNING));
    }

    /**
     * 转换得到piper服务的Master
     * @param piper
     * @return
     */
    public static String buildPiperStartStatePath(NamePiper piper) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s%s", piper.getDc(),piper.getUnitCategory(),
                piper.getUnit(), ZK_SWORD_PIPER, piper.getGroup(), ZK_SWORD_PIPER_MASTER, ZK_SWORD_PIPER_MASTER_STATER_STATE));
    }
}
