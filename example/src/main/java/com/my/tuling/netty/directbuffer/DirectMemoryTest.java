package com.my.tuling.netty.directbuffer;

import java.nio.ByteBuffer;

/**
 * 直接内存与堆内存的区别
 * . 适用场景对比
 * 场景	              DirectByteBuffer	             HeapByteBuffer
 * 大文件读写/网络传输	    ✅ 零拷贝，高性能	              ❌ 需要内存拷贝
 * 高频创建/销毁小缓冲区	❌ 分配成本高（调用 malloc()）	  ✅ 分配更快（堆内 TLAB 优化）
 * 与 JNI/Native 代码交互	✅ 直接传递内存地址	              ❌ 需要拷贝到堆外
 * 纯内存计算（无 I/O）	❌ 访问需通过 JNI，略慢	          ✅ 直接访问堆内存，更快
 */
public class DirectMemoryTest {

    public static void heapAccess() {/* 堆内存 - 访问 */
        long startTime = System.currentTimeMillis();
        //分配堆内存
        ByteBuffer buffer = ByteBuffer.allocate(1000); /* HeapByteBuffer */
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();

            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }
            buffer.clear();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("堆内存访问:" + (endTime - startTime) + "ms");
    }

    public static void directAccess() { /* 直接内存（堆外） - 访问 */
        long startTime = System.currentTimeMillis();
        //分配直接内存
        ByteBuffer buffer = ByteBuffer.allocateDirect(1000);/* DirectByteBuffer，可以减少IO的数据 在 内核态和用户态 之间 拷贝次数 */
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();
            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }
            buffer.clear();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("直接内存访问:" + (endTime - startTime) + "ms");
    }

    public static void heapAllocate() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            ByteBuffer.allocate(100);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("堆内存申请:" + (endTime - startTime) + "ms");
    }

    public static void directAllocate() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            ByteBuffer.allocateDirect(100);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("直接内存申请:" + (endTime - startTime) + "ms");
    }

    public static void main(String args[]) {
        for (int i = 0; i < 10; i++) {
            heapAccess();
            directAccess();
        }

        System.out.println();
/*
        for (int i = 0; i < 10; i++) {
            heapAllocate();
            directAllocate();
        }*/
    }
}