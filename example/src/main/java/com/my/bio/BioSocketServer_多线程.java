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
public class BioSocketServer_多线程 {

    /* BIO - 同步阻塞模型
    *  一个线程处理一个客户端请求，解决串行阻塞问题，但是无法解决c10k问题
    *  适合简单的、连接有限的通讯方式
    * */

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9000);
        while (true){
            log.info("server等待客户端连接...");
            Socket clientSocket = serverSocket.accept();/* 阻塞等待客户端连接 */
            log.info("server接收客户端["+clientSocket.getRemoteSocketAddress()+"]连接");
            new Thread(() -> {
                try {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void handleClient(Socket clientSocket) throws IOException{
        byte[] data = new byte[10];
        log.info("等待客户端["+clientSocket.getRemoteSocketAddress()+"]请求数据...");
        int read = clientSocket.getInputStream().read(data);/* 没有数据可读时，阻塞等待 */
        if(read!= -1){
            log.info("收到客户端["+clientSocket.getRemoteSocketAddress()+"]数据:"+ new String(data,0,read));
        }
    }
}
