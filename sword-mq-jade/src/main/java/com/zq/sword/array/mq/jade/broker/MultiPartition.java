package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.ResourceInputStream;
import com.zq.sword.array.stream.io.ResourceOutputStream;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;

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

    /**
     * 分片文件前缀
     */
    private static final String PARTITION_FILE_PREFIX = "partition-";

    private ResourceContainer container;

    private long id;

    private String name;

    private File partitionFile;

    /**
     * 段集合
     */
    private List<Segment> segments;

    /**
     * 是否正在写入数据
     */
    private volatile boolean writing = false;

    public MultiPartition(ResourceContainer container, long id) {
        this.container = container;
        this.id = id;
        this.name = PARTITION_FILE_PREFIX + id;
        this.partitionFile = new File(container.getResourceLocation() + File.separator + name);
        this.segments = new ArrayList<>();
    }

    public MultiPartition(ResourceContainer container, File partitionFile) {
        this.container = container;
        this.partitionFile = partitionFile;
        this.name = partitionFile.getName();
        this.id = Long.parseLong(partitionFile.getName().substring(PARTITION_FILE_PREFIX.length()));
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
    public String name() {
        return name;
    }

    @Override
    public String path() {
        return partitionFile.getPath();
    }

    @Override
    public PartitionInputStream openInputStream() throws InputStreamOpenException {
        return new PartitionInputStream(this);
    }

    @Override
    public PartitionOutputStream openOutputStream() throws OutputStreamOpenException {
        if(writing){
            throw new OutputStreamOpenException("partition is writing");
        }
        writing = true;
        return new PartitionOutputStream(this);
    }

    @Override
    public void reset() {
        for (Segment segment : segments){
            segment.reset();
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
    public static class PartitionInputStream implements ResourceInputStream, ObjectInputStream{

        private MultiPartition partition;

        /**
         * 消息ID
         */
        private long msgId;

        public PartitionInputStream(MultiPartition partition) {
            this.partition = partition;
        }

        /**
         * 定位
         * @param msgId
         * @return
         */
        public boolean location(long msgId){
            this.msgId = msgId;
            return partition.findSegment(msgId) != null;
        }

        @Override
        public void skip(long offset) throws IOException {
        }

        @Override
        public long offset() throws IOException {
            return 0;
        }

        @Override
        public int readInt() throws IOException {
            return 0;
        }

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public int read(byte[] data) throws IOException {
            return 0;
        }

        @Override
        public int read(byte[] data, int offset, int len) throws IOException {
            return 0;
        }

        @Override
        public long available() throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {

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
            SeqFileSegment sequenceFileSegment = (SeqFileSegment)partition.findSegment(msgId);
            if(sequenceFileSegment == null){
                throw new IOException("msgId is not find in partition");
            }
            SeqFileSegment.SegmentInputStream inputStream = null;
            try {
                inputStream = sequenceFileSegment.openInputStream();
                boolean locSuccess =  inputStream.location(msgId);
                if(!locSuccess){
                    throw new IOException("msgId is not find in segment");
                }
                //读当前定位到的msg
                inputStream.readObject();
                int i = 0;
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
    }

    /**
     * 消息输入流
     */
    public static class PartitionOutputStream implements ResourceOutputStream, ObjectOutputStream{

        private MultiPartition partition;

        public PartitionOutputStream(MultiPartition partition) {
            this.partition = partition;
        }

        @Override
        public void skip(long offset) throws IOException {

        }

        @Override
        public void writeInt(int data) throws IOException {

        }

        @Override
        public void writeBytes(byte[] data) throws IOException {

        }

        @Override
        public void write(byte[] data) throws IOException {

        }

        @Override
        public void write(byte[] data, int offset, int len) throws IOException {

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
                SeqFileSegment sequenceFileSegment = (SeqFileSegment)partition.getWritableSegment(msgId);
                SeqFileSegment.SegmentOutputStream outputStream = sequenceFileSegment.openOutputStream();
                for (Object o : objs){
                    if(sequenceFileSegment.isFull()){
                        outputStream.close();
                        sequenceFileSegment = (SeqFileSegment)partition.getWritableSegment(msgId);
                        outputStream = sequenceFileSegment.openOutputStream();
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
