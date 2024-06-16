package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.MessageModel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class RealtimeController {
    private SimpMessagingTemplate template;

    @MessageMapping("/message")
    @SendTo("/group/public")
    public MessageModel receiveMessage(@Payload MessageModel message) {
        template.convertAndSend("/group/"+ message.getChatEntity().getId(),message);
        return message;
    }
}
