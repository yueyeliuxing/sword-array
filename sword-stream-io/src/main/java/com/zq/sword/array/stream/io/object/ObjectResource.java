package com.zq.sword.array.stream.io.object;

import com.zq.sword.array.data.ObjectDeserializer;
import com.zq.sword.array.data.ObjectSerializer;
import com.zq.sword.array.stream.io.Resource;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;

/**
 * @program: sword-array
 * @description: bitcask存储
 * @author: zhouqi1
 * @create: 2019-01-14 10:58
 **/
public class ObjectResource implements Resource {

    /**
     * 实际的资源存储器
     */
    private Resource resource;

    /**
     * 对象序列化器
     */
    private ObjectSerializer objectSerializer;

    /**
     * 对象反序列化器
     */
    private ObjectDeserializer objectDeserializer;

    public ObjectResource(Resource resource, ObjectSerializer objectSerializer, ObjectDeserializer objectDeserializer) {
        this.resource = resource;
        this.objectSerializer = objectSerializer;
        this.objectDeserializer = objectDeserializer;
    }

    @Override
    public ObjectResourceInputStream openInputStream() throws InputStreamOpenException {
        return new ObjectResourceInputStream(resource.openInputStream(), objectDeserializer);
    }

    @Override
    public ObjectResourceOutputStream openOutputStream() throws OutputStreamOpenException {
        return new ObjectResourceOutputStream(resource.openOutputStream(), objectSerializer);
    }

    @Override
    public void reset() {
        resource.reset();
    }

}