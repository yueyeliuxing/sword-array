package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.stream.io.AbstractResourceInputStream;
import com.zq.sword.array.stream.io.AbstractResourceOutputStream;
import com.zq.sword.array.stream.io.Resource;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @program: sword-array
 * @description: 备份分片资源
 * @author: zhouqi1
 * @create: 2019-01-17 16:57
 **/
public class DuplicatePartitionResource implements Resource {

    private Partition master;

    private List<Partition> slaves;

    public DuplicatePartitionResource(Partition master, List<Partition> slaves) {
        this.master = master;
        this.slaves = slaves;
    }

    @Override
    public ObjectInputStream openInputStream() throws InputStreamOpenException {
        return new DuplicatePartitionInputStream(master.openInputStream());
    }

    @Override
    public ObjectOutputStream openOutputStream() throws OutputStreamOpenException {
        return null;
    }

    @Override
    public void close() {

    }

    /**
     * 输入流
     */
    private class DuplicatePartitionInputStream extends AbstractResourceInputStream implements ObjectInputStream{

        private ObjectInputStream inputStream;

        public DuplicatePartitionInputStream(ObjectInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public Object readObject() throws IOException {
            return inputStream.readObject();
        }

        @Override
        public void readObject(Object[] objs) throws IOException {
            inputStream.readObject(objs);
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }

    /**
     * 输出流
     */
    private class DuplicatePartitionOutputStream extends AbstractResourceOutputStream implements ObjectOutputStream{

        private List<ObjectOutputStream> outputStreamsList;

        public DuplicatePartitionOutputStream(List<ObjectOutputStream> outputStreamsList) {
            this.outputStreamsList = outputStreamsList;
        }

        @Override
        public void writeObject(Object obj) throws IOException {
            if(outputStreamsList != null && !outputStreamsList.isEmpty()){
                for (ObjectOutputStream outputStream : outputStreamsList){
                    outputStream.writeObject(obj);
                }
            }
        }

        @Override
        public void writeObject(List<Object> objs) throws IOException {
            if(outputStreamsList != null && !outputStreamsList.isEmpty()){
                for (ObjectOutputStream outputStream : outputStreamsList){
                    outputStream.writeObject(objs);
                }
            }
        }

        @Override
        public void close() throws IOException {
            if(outputStreamsList != null && !outputStreamsList.isEmpty()){
                for (ObjectOutputStream outputStream : outputStreamsList){
                    outputStream.close();
                }
            }
        }
    }


}
