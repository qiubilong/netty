package com.my.netty.common;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author chenxuegui
 * @since 2025/4/17
 */

@Data
@Accessors(chain = true)
@ToString
public class RpcMessage {

    public int len;

    public int type; //消息类型，1=String（心跳等）, 0=PB对象字节流

    public byte[] content;
}
