package com.my.netty.common;

import com.my.tuling.netty.codec.ProtostuffUtil;
import io.netty.channel.Channel;

import java.nio.charset.StandardCharsets;

/**
 * @author chenxuegui
 * @since 2025/4/17
 */
public class RpcMessageUtil {

    public static void writeAndFlushInner(Channel channel,String msg){
        RpcMessage rpcMessage = new RpcMessage();
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        rpcMessage.setLen(bytes.length + 4);
        rpcMessage.setType(1);
        rpcMessage.setContent(bytes);

        channel.writeAndFlush(rpcMessage);
    }

    public static void writeAndFlush(Channel channel,String msg){
        UserMessage userMessage = new UserMessage(channel.localAddress().toString(),msg);
        writeAndFlush(channel,userMessage);
    }

    public static void writeAndFlush(Channel channel,UserMessage userMessage){
        RpcMessage rpcMessage = new RpcMessage();
        byte[] bytes = ProtostuffUtil.serializer(userMessage);
        rpcMessage.setLen(bytes.length + 4);
        rpcMessage.setType(0);
        rpcMessage.setContent(bytes);

        channel.writeAndFlush(rpcMessage);
    }
}
