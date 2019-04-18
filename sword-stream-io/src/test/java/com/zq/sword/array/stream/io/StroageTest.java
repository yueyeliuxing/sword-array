package com.zq.sword.array.stream.io;

import com.zq.sword.array.stream.io.storage.engine.KVStorageEngine;
import com.zq.sword.array.stream.io.storage.engine.SeqFileKVStorageEngine;
import org.junit.Before;
import org.junit.Test;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2018-11-05 16:17
 **/
public class StroageTest {


    private KVStorageEngine<byte[], byte[]> kvStorageEngine;

    @Before
    public void setUp(){
        kvStorageEngine = new SeqFileKVStorageEngine("E:\\storage");
    }

    @Test
    public void testServer(){

        kvStorageEngine.insert("zq".getBytes(), "123".getBytes());

        String value = new String(kvStorageEngine.find("zq".getBytes()));
        System.out.println(value);
    }

    @Test
    public void testClient()throws Exception{
    }

}