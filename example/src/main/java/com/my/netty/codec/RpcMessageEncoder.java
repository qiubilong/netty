package com.my.netty.codec;

import com.my.netty.common.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenxuegui
 * @since 2025/4/17
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {


    @Override /* 自定义对象 --> TCP字节流 */
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getLen());
        out.writeInt(msg.getType());
        out.writeBytes(msg.getContent());

    }
}
