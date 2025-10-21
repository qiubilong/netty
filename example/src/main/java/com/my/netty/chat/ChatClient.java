package com.my.netty.chat;

import com.my.netty.codec.RpcMessageDecoder;
import com.my.netty.codec.RpcMessageEncoder;
import com.my.netty.common.HeartbeatMessage;
import com.my.netty.common.RpcMessageUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;


/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class ChatClient {

    private int reConnectTries = 0;
    private int reConnectBaseSec = 5;


    Bootstrap bootstrap;
    NioEventLoopGroup work;
    Channel channel;


    public ChatClient() {
        work = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(work);

        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception { //建立连接后，初始化该通道的handler处理链

                ChannelPipeline pipeline = ch.pipeline();

                //outBound - 对象转字节流
                pipeline.addLast("RpcMessageEncoder", new RpcMessageEncoder());

                // inBound - TCP字节流解码
                pipeline.addLast("RpcMessageDecoder", new RpcMessageDecoder());
                pipeline.addLast("ChatClientHandler", new ChatClientHandler());
            }
        });
    }

    public Channel connect() throws Exception {
        log.info("connect server...");
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    reConnectTries = 0;
                    log.info("连接服务器成功");
                    doKeepAlive();//心跳保活
                }else {
                    log.info("连接服务器失败");
                    doReConnect();//失败重连
                }
            }
        });
        channelFuture.sync();
        channel = channelFuture.channel();
        return channel;
    }

    public void doKeepAlive(){ //发送保活心跳
        work.schedule(() -> {
            if(channel!=null && channel.isActive()){
                RpcMessageUtil.writeAndFlushInner(channel, HeartbeatMessage.PING);
                doKeepAlive();
            }

        },30, TimeUnit.SECONDS);

    }


    public void doReConnect(){//客户端重连
        if(reConnectTries <3){
            reConnectTries++;
            int delay = Math.min(reConnectBaseSec + reConnectTries * 2,30);
            log.info("doReConnect reConnectTries={},delay={}",reConnectTries,delay);
            work.schedule(() -> {
                if(channel == null || !channel.isActive()){
                    try {
                        connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            },delay,TimeUnit.SECONDS);
        }
    }

    public void close() throws InterruptedException {
        channel.close().sync();
        work.shutdownGracefully();
    }

    public static void main(String[] args)  throws Exception{

        NioEventLoopGroup work = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work);

            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    ChannelPipeline pipeline = ch.pipeline();

                    //outBound - 对象转字节流
                    pipeline.addLast("RpcMessageEncoder",new RpcMessageEncoder());

                    // inBound - TCP字节流解码
                    pipeline.addLast("RpcMessageDecoder",new RpcMessageDecoder());
                    pipeline.addLast("ChatClientHandler",new ChatClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            Channel channel = channelFuture.channel();

            for (int i = 0; i < 10; i++) {
                RpcMessageUtil.writeAndFlush(channel,"hello world!"+i);//测试拆分包
            }
            channel.closeFuture().sync();
        }finally {
            work.shutdownGracefully();
        }
    }
}
