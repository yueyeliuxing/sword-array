package com.zq.sword.array.network.rpc.framework.coder;

import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.framework.message.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: netty 消息解码器
 * @author: zhouqi1
 * @create: 2018-07-06 11:56
 **/
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private Logger logger = LoggerFactory.getLogger(NettyMessageDecoder.class);

    private NettyMarshallingDecoder nettyMarshallingDecoder;


    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        nettyMarshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
         ByteBuf frame = (ByteBuf) super.decode(ctx, in);
         if(frame == null){
             logger.error("frame is null ");
             return null;
         }
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());
        int size = frame.readInt();
        if(size > 0){
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for(int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key  = new String(keyArray, "UTF-8");
                attch.put(key, nettyMarshallingDecoder.decode(ctx, frame));
            }
            header.setAttachment(attch);
        }
        if(frame.readableBytes() > 12) {
            message.setBody(nettyMarshallingDecoder.decode(ctx, frame));
        }
        message.setHeader(header);
        return message;
    }
}
