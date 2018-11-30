package com.zq.sword.array.transfer.handler;

import com.zq.sword.array.transfer.message.Header;
import com.zq.sword.array.transfer.message.MessageType;
import com.zq.sword.array.transfer.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 登陆验证返回消息处理器
 * @author: zhouqi1
 * @create: 2018-07-06 14:30
 **/
public class LoginAuthRespHandler extends TransferHandler {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    private String[] whitekList = null;//{"127.0.0.1", "192.168.100.121"};

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;

        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            TransferMessage loginResp = null;
            if(nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte)-1);
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                if(whitekList != null && whitekList.length > 0){
                    for (String WIP : whitekList){
                        if(WIP.equals(ip)){
                            isOK = true;
                            break;
                        }
                    }
                }else {
                    isOK = true;
                }

                loginResp = isOK ? buildResponse((byte)0) : buildResponse((byte)-1);
                if(isOK){
                    nodeCheck.put(nodeIndex, true);
                }
            }
            System.out.println("The login response is :" + loginResp + "body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildResponse(byte body) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(body);
        return message;
    }
}
