package com.zq.sword.array.id;

/**
 * 分布式ID生成器
 */
public interface IdGenerator {
    /**
     * 获取分布式ID
     * @return
     */
    long nextId();
}
