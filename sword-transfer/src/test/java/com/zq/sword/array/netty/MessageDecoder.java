package com.zq.sword.array.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @program: sword-array
 * @description: netty 消息解码器
 * @author: zhouqi1
 * @create: 2018-07-06 11:56
 **/
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
      /*  ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if(frame == null){
            return null;
        }*/
        int len = in.readInt();
        byte[] msgBytes = new byte[len];
        in.readBytes(msgBytes);
        return new String(msgBytes);
    }
}
