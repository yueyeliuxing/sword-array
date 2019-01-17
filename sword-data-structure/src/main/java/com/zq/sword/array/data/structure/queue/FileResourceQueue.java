package com.zq.sword.array.data.structure.queue;

import com.zq.sword.array.data.ObjectDeserializer;
import com.zq.sword.array.data.ObjectSerializer;
import com.zq.sword.array.stream.io.file.FileResource;
import com.zq.sword.array.stream.io.object.ObjectResource;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: sword-array
 * @description: 文件资源对列
 * @author: zhouqi1
 * @create: 2019-01-17 19:40
 **/
public class FileResourceQueue<T> extends AbstractResourceQueue<T> implements ResourceQueue<T> {

    public FileResourceQueue(String fileLocation, ObjectSerializer<T> serializer, ObjectDeserializer<T> deserializer){
        this(new LinkedBlockingQueue<>(), new ObjectResource(new FileResource(fileLocation), serializer, deserializer));
    }

    public FileResourceQueue(Queue<T> queue, ObjectResource objectResource){
        super(queue, objectResource);
    }
}
