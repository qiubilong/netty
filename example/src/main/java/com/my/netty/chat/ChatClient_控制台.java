package com.my.netty.chat;

import com.my.netty.common.RpcMessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;


/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class ChatClient_控制台 {

    public static void main(String[] args)  throws Exception{
        ChatClient chatClient = new ChatClient();
        Channel channel = chatClient.connect();

        Scanner scanner = new Scanner(System.in);
        while (channel.isActive()) {
            String line = scanner.nextLine();
            RpcMessageUtil.writeAndFlush(channel,line);
        }
    }
}
