package com.zq.sword.array.stream.io.storage;

/**
 * @program: sword-array
 * @description: 存储引擎
 * @author: zhouqi1
 * @create: 2019-04-17 14:33
 **/
public interface KVStorageEngine<K, V> {

    /**
     * 数据添加
     * @param key
     * @param value
     */
    boolean insert(K key, V value);

    /***
     * 数据删除
     * @param key
     */
    boolean delete(K key);

    /**
     * 数据修改
     * @param key
     */
    boolean update(K key, V value);

    /**
     * 数据查询
     * @param key
     * @return
     */
    V find(K key);

}
