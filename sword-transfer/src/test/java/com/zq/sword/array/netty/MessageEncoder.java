package com.zq.sword.array.netty;

import com.zq.sword.array.netty.coder.MarshallingCodeCFactory;
import com.zq.sword.array.netty.coder.NettyMarshallingEncoder;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.TransferMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 消息编码
 * @author: zhouqi1
 * @create: 2018-07-06 10:40
 **/
public final class MessageEncoder extends MessageToMessageEncoder<String> {


    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        ByteBuf sendBuf = Unpooled.buffer();
        byte[] msgBytes = msg.getBytes();
        sendBuf.writeInt(msgBytes.length);
        sendBuf.writeBytes(msgBytes);
        out.add(sendBuf);
    }
}
