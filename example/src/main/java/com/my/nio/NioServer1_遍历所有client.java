package com.my.nio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class NioServer1_遍历所有client {

    /*  NIO - 同步非阻塞模型
     *  一个线程可以处理多个请求请求，但是需要遍历所有客户端，找出需要处理的客户端，效率低
     */
    static List<SocketChannel>  channelGroup = new ArrayList<>(100);
    public static void main(String[] args) throws Exception{

        ServerSocketChannel serverSocket = ServerSocketChannel.open();//ServerSocketChannel具体实现类，window/linux不同系统不同

        serverSocket.bind(new InetSocketAddress(9000),128);
        serverSocket.configureBlocking(false);
        log.info("Server启动成功");

        while (true){
            log.info("Server等待客户端建立连接...");
            SocketChannel clientSocket = serverSocket.accept();/* 非阻塞模式的accept不会阻塞，由操作系统内核accept()实现 */
            if(clientSocket!=null){
                log.info("客户端["+clientSocket.getRemoteAddress()+"]建立连接");
                clientSocket.configureBlocking(false);
                channelGroup.add(clientSocket);
            }

            /* 循环遍历所有的client，判断是否可读 */
            Iterator<SocketChannel> channelIterator = channelGroup.iterator();
            while (channelIterator.hasNext()) {
                SocketChannel socketChannel = channelIterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                int read = socketChannel.read(byteBuffer);/* 非阻塞模式的read不会阻塞 */
                if(read!=-1){
                    if(read>0){
                        log.info("收到客户端["+socketChannel.getRemoteAddress()+"]数据: " + new String(byteBuffer.array(),0,read));
                    }
                }else {
                    channelIterator.remove();
                    log.info("客户端["+socketChannel.getRemoteAddress()+"]断开连接-------");
                }
            }

        }
    }
}
