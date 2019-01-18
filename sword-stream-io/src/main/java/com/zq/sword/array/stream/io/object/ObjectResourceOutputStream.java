package com.zq.sword.array.stream.io.object;

import com.zq.sword.array.stream.io.AbstractResourceOutputStream;
import com.zq.sword.array.stream.io.ResourceOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 对象资源输出流
 * @author: zhouqi1
 * @create: 2019-01-14 12:42
 **/
public class ObjectResourceOutputStream extends AbstractResourceOutputStream implements ObjectOutputStream {

    private ResourceOutputStream outputStream;

    private DataSeparator dataSeparator;

    private ObjectSerializer objectSerializer;

    public ObjectResourceOutputStream(ResourceOutputStream outputStream, ObjectSerializer objectSerializer) {
        this.outputStream = outputStream;
        this.objectSerializer = objectSerializer;
    }

    public ObjectResourceOutputStream(ResourceOutputStream outputStream, DataSeparator dataSeparator, ObjectSerializer objectSerializer) {
        this(outputStream, objectSerializer);
        this.dataSeparator = dataSeparator;
    }

    @Override
    public void skip(long offset) throws IOException {
        this.outputStream.skip(offset);
    }

    @Override
    public void writeInt(int data) throws IOException {
        this.outputStream.writeInt(data);
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {
        this.outputStream.writeBytes(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        this.outputStream.write(data);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        this.outputStream.write(data, offset, len);
    }


    @Override
    public void writeObject(Object obj) throws IOException {
        List<Object> objs = new ArrayList<>();
        objs.add(obj);
        writeObject(objs);
    }

    @Override
    public void writeObject(List<Object> objs) throws IOException {
        if(objs != null && !objs.isEmpty()){
            for (Object obj : objs){
                byte[] dataArray = objectSerializer.serialize(obj);
                if(dataSeparator == null){
                    this.outputStream.writeInt(dataArray.length);
                    this.outputStream.write(dataArray);
                }else {
                    //获取分隔符
                    String character  = dataSeparator.character();
                    this.outputStream.write(dataArray);
                    this.outputStream.writeBytes(character.getBytes());
                }
            }
        }

    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}
