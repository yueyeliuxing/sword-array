package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.msg.MessageDeserializer;
import com.zq.sword.array.mq.jade.msg.MessageSerializer;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.file.FileResource;
import com.zq.sword.array.stream.io.file.FileResourceInputStream;
import com.zq.sword.array.stream.io.file.FileResourceOutputStream;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import com.zq.sword.array.stream.io.object.ObjectResourceInputStream;
import com.zq.sword.array.stream.io.object.ObjectResourceOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 顺序文件段
 * @author: zhouqi1
 * @create: 2019-01-16 10:44
 **/
public class SeqFileSegment implements Segment {

    private Logger logger = LoggerFactory.getLogger(SeqFileSegment.class);

    /**
     * 文件后缀
     */
    private static final String SEGMENT_FILE_NAME_SUFFIX = ".segment";

    /**
     * 段最大长度
     */
    private static final int SEGMENT_MAX_LENGTH = 64 * 1024 * 1024;

    private Partition partition;

    private Segment next;

    private long id;

    private String name;

    private Map<Long, Long> messageOffsets;

    private File segmentFile;

    /**
     * 创建一个空的段
     * @param partition
     * @param id
     */
    public SeqFileSegment(Partition partition, long id) {
        this.partition = partition;
        this.id = id;
        this.name = id+SEGMENT_FILE_NAME_SUFFIX;
        this.segmentFile = new File(partition.path() + File.separator + this.name);
        this.messageOffsets = new HashMap<>();
        this.segmentFile.getParentFile().mkdirs();

    }

    /**
     * 以指定的数据文件创建已存在的段
     * @param partition
     * @param segmentFile
     */
    public SeqFileSegment(Partition partition, File segmentFile){
        this.partition = partition;
        this.segmentFile = segmentFile;
        this.name = segmentFile.getName();
        this.id = Long.parseLong(segmentFile.getName().substring(0, segmentFile.getName().indexOf(SEGMENT_FILE_NAME_SUFFIX)));
        this.messageOffsets = loadMessageOffsets();
    }

    /**
     * 读文件获取索引
     * @return
     */
    private Map<Long, Long> loadMessageOffsets() {
        Map<Long, Long> messageOffsets = new HashMap<>();
        FileResource fileResource = new FileResource(segmentFile);
        ObjectResourceInputStream inputStream = null;
        try {
            inputStream = new ObjectResourceInputStream(fileResource.openInputStream(), new MessageDeserializer());
            Message message = null;
            do{
                long offset = inputStream.offset();
                message = (Message) inputStream.readObject();
                if(message != null){
                    messageOffsets.put(message.getMsgId(), offset);
                }
            }while (message != null);
        } catch (InputStreamOpenException e) {
            logger.error("打开文件失败", e);
        } catch (IOException e) {
            logger.error("读取文件失败", e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("打开文件流失败", e);
            }
        }

        return messageOffsets;
    }

    @Override
    public Segment next() {
        return next;
    }

    @Override
    public void next(Segment next) {
        this.next = next;
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
        return segmentFile.getPath();
    }

    @Override
    public long length() {
        return segmentFile.length();
    }

    @Override
    public boolean isFull() {
        return length() >= SEGMENT_MAX_LENGTH;
    }

    @Override
    public long lastModifyTime() {
        return segmentFile.lastModified();
    }

    @Override
    public ObjectInputStream openInputStream() throws InputStreamOpenException {
        try {
            return new SegmentInputStream(this);
        } catch (IOException e) {
            throw new InputStreamOpenException(e.getMessage());
        }
    }

    @Override
    public ObjectOutputStream openOutputStream() throws OutputStreamOpenException {
        try {
            return new SegmentOutputStream(this);
        } catch (IOException e) {
            throw new OutputStreamOpenException(e.getMessage());
        }
    }

    @Override
    public void close() {
        messageOffsets.clear();
        segmentFile.delete();
    }

    /**
     * 通过消息ID获得当前消息在文件中的偏移量
     * @param msgId
     * @return
     */
    private long findOffset(long msgId){
        return messageOffsets.get(msgId);
    }

    /**
     * 消息输入流
     */
    private class SegmentInputStream extends ObjectResourceInputStream {

        private SeqFileSegment segment;

        public SegmentInputStream(SeqFileSegment segment) throws IOException {
            super(new FileResourceInputStream(segment.segmentFile), new MessageDeserializer());
            this.segment = segment;
        }

        @Override
        public void skip(long msgId) throws IOException {
            Long offset = segment.findOffset(msgId);
            if(offset == null){
                throw new IllegalArgumentException(String.format("msgId:%s is not find in segment", msgId));
            }
            super.skip(offset);
        }
    }

    /**
     * 消息输出流
     */
    private class SegmentOutputStream extends ObjectResourceOutputStream {

        public SegmentOutputStream(SeqFileSegment segment) throws IOException {
            super(new FileResourceOutputStream(segment.segmentFile), new MessageSerializer());
        }
    }
}
