package com.my.netty.common;

import lombok.Data;
import lombok.ToString;

/**
 * @author chenxuegui
 * @since 2025/4/16
 */
@Data
@ToString
public class UserMessage {
    private String clientId;
    private String message;

    private Long time = System.currentTimeMillis();

    public UserMessage() {
    }

    public UserMessage(String clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }
}
