package com.my.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Slf4j
public class BioSocketServer {

    /* BIO - 同步阻塞模型
     * 阻塞等待客户端的连接、读、写事件
     * 不能同时处理多个客户端请求，只能循环串行处理
    * */

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9000);
        while (true){
            log.info("server等待客户端连接...");
            Socket clientSocket = serverSocket.accept();/* 阻塞等待客户端连接 */
            log.info("server接收客户端["+clientSocket.getRemoteSocketAddress()+"]连接");
            handleClient(clientSocket);
        }
    }

    public static void handleClient(Socket clientSocket) throws IOException{
        byte[] data = new byte[10];
        log.info("等待客户端["+clientSocket.getRemoteSocketAddress()+"]请求数据...");
        int read = clientSocket.getInputStream().read(data);/* 没有数据可读时，阻塞等待 */
        if(read!= -1){
            log.info("收到客户端["+clientSocket.getRemoteSocketAddress()+"]数据:"+ new String(data));
        }
    }
}
