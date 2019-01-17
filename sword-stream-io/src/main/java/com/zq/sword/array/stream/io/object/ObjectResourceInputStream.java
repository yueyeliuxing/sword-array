package com.zq.sword.array.stream.io.object;

import com.zq.sword.array.data.ObjectDeserializer;
import com.zq.sword.array.stream.io.AbstractResourceInputStream;
import com.zq.sword.array.stream.io.ResourceInputStream;

import java.io.EOFException;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 对象输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:40
 **/
public class ObjectResourceInputStream extends AbstractResourceInputStream implements ObjectInputStream {

    /**
     * 具体的资源输入流
     */
    private ResourceInputStream inputStream;

    /**
     * 对象存储分隔符
     */
    private DataSeparator dataSeparator;

    /**
     * 对象反序列化器
     */
    private ObjectDeserializer objectDeserializer;


    public ObjectResourceInputStream(ResourceInputStream inputStream, ObjectDeserializer objectDeserializer) {
        this.inputStream = inputStream;
        this.objectDeserializer = objectDeserializer;
    }

    public ObjectResourceInputStream(ResourceInputStream inputStream, DataSeparator dataSeparator, ObjectDeserializer objectDeserializer) {
        this(inputStream, objectDeserializer);
        this.dataSeparator = dataSeparator;
    }

    @Override
    public void skip(long offset) throws IOException {
        inputStream.skip(offset);
    }

    @Override
    public long offset() throws IOException {
        return inputStream.offset();
    }

    @Override
    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(byte[] data) throws IOException {
        return inputStream.read(data);
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        return inputStream.read(data, offset, len);
    }

    @Override
    public long available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public Object readObject() throws IOException {
        Object[] objs = new Object[1];
        readObject(objs);
        return objs[0];
    }

    @Override
    public void readObject(Object[] objs) throws IOException{
        int n = 0, num = objs.length;
        while (num >0 && n < num){
            //分隔符为空 默认是长度分隔
            if(dataSeparator == null){
                try{
                    int itemLen = inputStream.readInt();
                    byte[] itemArray = new byte[itemLen];
                    int l = inputStream.read(itemArray);
                    if(l > -1){
                        objs[n++] = objectDeserializer.deserialize(itemArray);
                    }else {
                        break;
                    }
                }catch (EOFException e){
                    break;
                }

            }else {
                byte[] temp = new byte[1024];
                int line = 0;
                StringBuilder sb = new StringBuilder();
                int p = 0;
                while ((line = inputStream.read(temp)) != -1) {
                    sb.append(new String(temp,0,line));
                    byte[] data = sb.toString().getBytes();
                    int index = dataSeparator.isBoundary(data);
                    if(index > -1){
                        byte[] item = dataSeparator.toDataArray(data);
                        int len = index+dataSeparator.character().length();
                        if(len < data.length){
                            sb = new StringBuilder();
                            for(int i = len; i < data.length; i++){
                                sb.append(data[i]);
                            }
                        }
                        objs[n++] = objectDeserializer.deserialize(item);
                    }else {
                        break;
                    }
                }
                break;
            }
        }
    }


}
