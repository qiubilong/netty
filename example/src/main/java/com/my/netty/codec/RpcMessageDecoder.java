package com.my.netty.codec;

import com.my.netty.common.HeartbeatMessage;
import com.my.netty.common.RpcMessage;
import com.my.netty.common.RpcMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author chenxuegui
 * @since 2025/4/17
 *
 * TCP字节流 - 解码分包
 *
 * 数据累积
 * Netty 会将未消费的数据保留在 ByteBuf in 中，直到：
 *  - 解码成功（调用 out.add() 并消费数据）。
 *  - 连接关闭（自动释放缓冲区）。
 *
 * 流量控制 - 如果 decode 不消费数据，TCP 接收窗口会逐渐填满，最终抑制对端发送（TCP 流控机制）。
 * 内存保护 - Netty 默认会对 ByteBuf 进行扩容，但需避免长期不释放（可通过 in.release() 或 ReferenceCountUtil.release(in) 手动释放）。
 */
@Slf4j
public class RpcMessageDecoder extends ByteToMessageDecoder {

    int readIdleTimes = 0;


    int length;
    int type;

    @Override /* ByteBuf in 参数代表的是 从 TCP 缓冲区读取到的原始字节数据 */
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        readIdleTimes = 0;
        log.info("RpcMessageDecoder ByteBuf={}",in);
        if(in.readableBytes()>8){
            if(this.length ==0){
                this.length = in.readInt() - 4;
                this.type = in.readInt();
            }

            if(in.readableBytes()<length){
                log.info("RpcMessageDecoder wait length={},ByteBuf={}",length,in);
                return;
            }

            byte[] content = new byte[length];
            in.readBytes(content);
            if(this.type == 1){
                //响应心跳
                String innerMessage = new String(content);
                log.info("RpcMessageDecoder inner type={},clientIp=[{}], length={}，message={}", type,ctx.channel().remoteAddress(),length,innerMessage);
                if(HeartbeatMessage.PING.equals(innerMessage)){
                    RpcMessageUtil.writeAndFlushInner(ctx.channel(), HeartbeatMessage.PONG);
                }
                this.type = 0;
                this.length = 0;
                return;
            }

            RpcMessage rpcMessage = new RpcMessage() ;
            rpcMessage.setLen(length).setType(type).setContent(content);
            //log.info("RpcMessageDecoder rpc message={}",rpcMessage);
            out.add(rpcMessage);/* 解码成功， 传递到下一个Handler */

            this.type = 0;
            this.length = 0;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        Channel channel = ctx.channel();
        String eventType = null;
        switch (event.state()) {
            case READER_IDLE:
                eventType = "读空闲";
                readIdleTimes++; // 读空闲的计数加1
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                // 不处理
                break;
        }


        log.info("客户端["+channel.remoteAddress()+"]超时事件：" + eventType+",readIdleTimes="+readIdleTimes);
        if (readIdleTimes > 3) {
            log.info("客户端["+channel.remoteAddress()+"]读空闲超过3次，关闭连接，释放更多资源");
            RpcMessageUtil.writeAndFlushInner(channel,"idle close");
            channel.close();
        }
    }
}
