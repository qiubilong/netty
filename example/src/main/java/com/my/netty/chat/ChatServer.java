package com.my.netty.chat;

import com.my.netty.codec.RpcMessageDecoder;
import com.my.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class ChatServer {

    public static void main(String[] args) throws Exception {


        EventLoopGroup boss = new NioEventLoopGroup(1);/* 处理客户端连接accept */
        EventLoopGroup work = new NioEventLoopGroup();/* 处理客户端读写 */   //默认核心数2倍

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,work);//Reactor主从模式

            serverBootstrap.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG,128);
            /* ChannelInitializer 本质是一个 InboundHandler */
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() { /* 客户端Channel建立后，（注册到IO多路复用器），回调管道初始化 ChannelInitializer */
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //outBound - 对象转字节流
                    pipeline.addLast("RpcMessageEncoder",new RpcMessageEncoder());

                    // inBound - TCP字节流解码
                    pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));//心跳保活检查，下一个Handler必须实现userEventTriggered
                    pipeline.addLast("RpcMessageDecoder",new RpcMessageDecoder());
                    pipeline.addLast("ChatServerHandler",new ChatServerHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();
            log.info("ChatServer 启动成功...");
            channelFuture.channel().closeFuture().sync();
            log.info("ChatServer 关闭...");
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }
}
