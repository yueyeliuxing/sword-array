package com.zq.sword.array.netty.coder;

import com.zq.redis.piper.netty.message.Header;
import com.zq.redis.piper.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: netty 消息解码器
 * @author: zhouqi1
 * @create: 2018-07-06 11:56
 **/
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private NettyMarshallingDecoder nettyMarshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        nettyMarshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
         ByteBuf frame = (ByteBuf) super.decode(ctx, in);
         if(frame == null){
             return null;
         }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if(size > 0){
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for(int i = 0; i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key  = new String(keyArray, "UTF-8");
                attch.put(key, nettyMarshallingDecoder.decode(ctx, in));
            }
            header.setAttachment(attch);
        }
        if(in.readableBytes() > 4) {
            message.setBody(nettyMarshallingDecoder.decode(ctx, in));
        }
        message.setHeader(header);
        return message;
    }
}
