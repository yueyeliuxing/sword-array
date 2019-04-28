package com.zq.sword.array.network.rpc.framework.coder;

import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.framework.message.Header;
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
public final class NettyMessageEncoder extends MessageToMessageEncoder<TransferMessage> {

    private NettyMarshallingEncoder nettyMarshallingEncoder;

    public NettyMessageEncoder() {
        nettyMarshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferMessage msg, List<Object> out) throws Exception {
        if(msg == null || msg.getHeader() == null) {
            throw new Exception("The encode message is null");
        }

        ByteBuf sendBuf = Unpooled.buffer();
        Header header = msg.getHeader();
        sendBuf.writeInt(header.getCrcCode());
        sendBuf.writeInt(header.getLength());
        sendBuf.writeLong(header.getSessionID());
        sendBuf.writeByte(header.getType());
        sendBuf.writeByte(header.getPriority());
        sendBuf.writeInt(header.getAttachment().size());
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for(Map.Entry<String, Object> param : header.getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            nettyMarshallingEncoder.encode(ctx, value, sendBuf);
        }
        if(msg.getBody() != null) {
            nettyMarshallingEncoder.encode(ctx, msg.getBody(), sendBuf);
        }else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes());
        sendBuf.writeLong(0L);

        out.add(sendBuf);
    }
}
