package com.my.netty.chat;

import com.my.netty.common.RpcMessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class ChatClient_拆解包 {

    public static void main(String[] args)  throws Exception{

        ChatClient chatClient = new ChatClient();
        Channel channel = chatClient.connect();

        for (int i = 0; i < 10; i++) {
            RpcMessageUtil.writeAndFlush(channel,"hello world!"+i);//测试拆分包
        }

        channel.closeFuture().sync();
    }
}
