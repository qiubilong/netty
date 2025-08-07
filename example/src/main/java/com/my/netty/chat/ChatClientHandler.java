package com.my.netty.chat;


import com.my.netty.common.RpcMessage;
import com.my.netty.common.UserMessage;
import com.my.tuling.netty.codec.ProtostuffUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage rpcMessage) throws Exception {
        Channel channel = ctx.channel();
        UserMessage userMessage = ProtostuffUtil.deserializer(rpcMessage.getContent(), UserMessage.class);
        log.info("收到消息 ："+ userMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("客户端["+channel.localAddress()+"]建立连接成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("客户端["+channel.localAddress()+"]断开连接");
        channel.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
