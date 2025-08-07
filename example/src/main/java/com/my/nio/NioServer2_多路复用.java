package com.my.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class NioServer2_多路复用 {

    /* IO多用复用器 -- Epoll - IO事件响应模型 -- 操作系统内核根据socket中断回调，将IO事件放在 Epoll多路复用器的readyList中
     * 只 处理有IO事件发生的客户端，不做低效的遍历
     */

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//创建ServerSocket具体实现对象，window、Linux不同操作系统不同
        serverSocketChannel.bind(new InetSocketAddress(9000), 512);
        serverSocketChannel.configureBlocking(false);//设置同步非阻塞模式

        /* 创建一个io多用复用器，不同操作系统实现类不同 */
        Selector selector = Selector.open();//Linux操作系统下 EPollSelectorProvider --> EPollSelectorImpl --> EPollArrayWrapper
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);/* 注册serverSocket的Accept事件 -- implRegister */
        log.info("Server启动成功");
        while (true){
            log.info("Server等待多路复用器的readyList的IO事件...");
            //真正注册Epoll监听事件，等待需要处理的IO事件发生。操作系统将channel发生的IO事件放在多路复用器的readyList就绪列表中  ————  epoll --> ctl -->wait
            selector.select();//EPollSelectorImpl.doSelect()-->  EPollArrayWrapper.epollCtl()-->epollWait()

            Set<SelectionKey> selectionKeys = selector.selectedKeys();/*IO事件集合, EPollSelectorImpl.doSelect()-->EPollArrayWrapper.epollWait() 返回后添加IO事件到selectedKeys集合 */
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isAcceptable()){/*  serverSocket的Accept事件 */
                    ServerSocketChannel serverSocket =  (ServerSocketChannel)selectionKey.channel();

                    SocketChannel clientSocket = serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector,SelectionKey.OP_READ);/* 注册client的Read事件 */
                    log.info("接收客户端["+clientSocket.getRemoteAddress()+"]建立连接");
                }else if(selectionKey.isReadable()){
                    SocketChannel clientSocket =  (SocketChannel)selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(18);
                    int read = clientSocket.read(byteBuffer);
                    if(read>0){
                        log.info("接收客户端["+clientSocket.getRemoteAddress()+"]数据: "+ new String(byteBuffer.array(),0,read));
                    }else if(read == -1){
                        log.info("客户端["+clientSocket.getRemoteAddress()+"]断开连接...");
                        clientSocket.close();
                    }
                }

                iterator.remove();//从事件集合里删除本次处理的key，防止下次selectedKeys()重复处理
            }

        }

    }
}
