package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.AbstractResourceInputStream;
import com.zq.sword.array.stream.io.AbstractResourceOutputStream;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 多路分片
 * @author: zhouqi1
 * @create: 2019-01-16 10:59
 **/
public class MultiPartition implements Partition {

    private Logger logger = LoggerFactory.getLogger(MultiPartition.class);

    /**
     * 分片文件前缀
     */
    private static final String PARTITION_FILE_PREFIX = "part-";

    private Broker broker;

    private long id;

    private String tag;

    private String name;

    private String topic;

    private File partitionFile;

    /**
     * 段集合
     */
    private List<Segment> segments;

    /**
     * 是否正在写入数据
     */
    private volatile boolean writing = false;

    public MultiPartition(Broker broker, String topic, String tag, long id) {
        this.broker = broker;
        this.topic = topic;
        this.tag = tag;
        this.id = id;
        this.name = PARTITION_FILE_PREFIX + tag + "-" + id;
        this.partitionFile = new File(broker.getResourceLocation() + File.separator + topic + File.separator + name);
        this.segments = new ArrayList<>();
    }

    public MultiPartition(Broker broker, File partitionFile) {
        this.broker = broker;
        this.partitionFile = partitionFile;
        this.name = partitionFile.getName();
        String[] params = partitionFile.getName().split("-");
        this.topic = partitionFile.getParentFile().getName();
        this.tag = params[1];
        this.id = Long.parseLong(params[2]);
        this.segments = loadSegments();
    }


    private List<Segment> loadSegments(){
        List<Segment> segments =  new ArrayList<>();
        if(!partitionFile.exists()){
            partitionFile.mkdirs();
            return segments;
        }

        File[] segmentFiles = partitionFile.listFiles();
        if(segmentFiles != null && segmentFiles.length > 0){
            int i = 0;
            for (File segmentFile : segmentFiles){
                segments.add(new SeqFileSegment(this, segmentFile));
                if(i > 0){
                    segments.get(i-1).next(segments.get(i));
                }
                i++;
            }
        }
        return segments;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String tag() {
        return tag;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String path() {
        return partitionFile.getPath();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public ObjectInputStream openInputStream() throws InputStreamOpenException {
        return new PartitionInputStream(this);
    }

    @Override
    public ObjectOutputStream openOutputStream() throws OutputStreamOpenException {
        if(writing){
            throw new OutputStreamOpenException("partition is writing");
        }
        writing = true;
        return new PartitionOutputStream(this);
    }

    @Override
    public void close() {
        for (Segment segment : segments){
            segment.close();
        }
        segments.clear();
    }

    /**
     * 获得一个可写的段
     * @param msgId
     * @return
     */
    private Segment getWritableSegment(long msgId){
        Segment segment = lastSegment();
        if (segment == null){
            segment = createSegment(msgId);
            segments.add(segment);
        }
        if(segment.isFull()){
            Segment newSegment = createSegment(msgId);
            segment.next(newSegment);
            segment = newSegment;
            segments.add(segment);
        }
        return segment;
    }

    /**
     * 创建一个段
     * @param msgId
     * @return
     */
    private Segment createSegment(long msgId){
        return new SeqFileSegment(this, msgId);
    }

    /**
     * 第一个段
     * @return
     */
    private Segment lastSegment(){
        return segments.isEmpty() ? null : segments.get(segments.size()-1);
    }

    /**
     * 获取消息Id所在的段
     * @param msgId
     * @return
     */
    private Segment findSegment(long msgId){
        if(segments != null && !segments.isEmpty()){
            if(msgId == 0){
                return segments.get(0);
            }
            for(int i = 0; i < segments.size(); i++){
                Segment segment = segments.get(i);
                long id  = segment.id();
                if(msgId >= id && (i == segments.size() - 1 ||  msgId < segments.get(i+1).id())){
                    return segment;
                }
            }
        }
        return null;
    }

    /**
     * 消息输入流
     */
    private class PartitionInputStream extends AbstractResourceInputStream implements ObjectInputStream{

        private MultiPartition partition;

        /**
         * 消息ID
         */
        private long msgId;

        public PartitionInputStream(MultiPartition partition) {
            this.partition = partition;
        }

        @Override
        public void skip(long msgId) throws IOException {
            this.msgId = msgId;
        }

        @Override
        public Object readObject() throws IOException {
            Object[] objs = new Object[1];
            readObject(objs);
            return objs[0];
        }

        @Override
        public void readObject(Object[] objs) throws IOException {
            int size = objs.length;
            Segment sequenceFileSegment = partition.findSegment(msgId);
            logger.info("通过消息ID->{}读取分片->{}", msgId, sequenceFileSegment == null ? null : sequenceFileSegment.id());
            if(sequenceFileSegment == null){
                if(msgId == 0){
                    return;
                }
                throw new IOException("msgId is not find in partition");
            }
            ObjectInputStream inputStream = null;
            try {
                inputStream = sequenceFileSegment.openInputStream();
                try{
                    inputStream.skip(msgId);
                }catch (Exception e){
                    logger.info(e.getMessage());
                    return;
                }

                int i = 0;
                //读当前定位到的msg
               /* Object obj = inputStream.readObject();
                if(msgId == 0){
                    objs[i++] = obj;
                }*/
                while (i < size){
                    //获取下一个msg
                    Object obj =   inputStream.readObject();
                    if(obj != null){
                        objs[i++] = obj;
                        msgId = ((Message)obj).getMsgId();
                    }else {
                        SeqFileSegment fileSegment = (SeqFileSegment) sequenceFileSegment.next();
                        if(fileSegment == null){
                            inputStream.close();
                            break;
                        }else {
                            inputStream = fileSegment.openInputStream();
                        }
                    }
                }
            } catch (InputStreamOpenException e) {
                throw new IOException(e.getMessage());
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
        }

        @Override
        public void close() throws IOException {

        }
    }

    /**
     * 消息输入流
     */
    private class PartitionOutputStream extends AbstractResourceOutputStream implements ObjectOutputStream{

        private MultiPartition partition;

        public PartitionOutputStream(MultiPartition partition) {
            this.partition = partition;
        }

        @Override
        public void writeObject(Object obj) throws IOException {
            List<Object> objs = new ArrayList<>();
            objs.add(obj);
            writeObject(objs);
        }

        @Override
        public void writeObject(List<Object> objs) throws IOException {
            try{
                Object obj = objs.get(0);
                Message message = (Message)obj;
                long msgId = message.getMsgId();
                Segment segment = partition.getWritableSegment(msgId);
                ObjectOutputStream outputStream = segment.openOutputStream();
                for (Object o : objs){
                    if(segment.isFull()){
                        outputStream.close();
                        segment = partition.getWritableSegment(msgId);
                        outputStream = segment.openOutputStream();
                    }
                    outputStream.writeObject(o);
                }
                outputStream.close();
            }catch (Exception e){
                throw new IOException(e.getMessage());
            }
        }

        @Override
        public void close() throws IOException {
            partition.writing = false;
        }
    }
}
