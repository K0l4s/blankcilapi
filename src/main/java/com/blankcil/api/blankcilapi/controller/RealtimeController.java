package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.MessageModel;
import com.blankcil.api.blankcilapi.model.request.SendMessageModal;
import com.blankcil.api.blankcilapi.service.ChatServiceImpl;
import com.blankcil.api.blankcilapi.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

@Controller
public class RealtimeController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private IChatService chatService = new ChatServiceImpl();

    private static final Logger logger = LoggerFactory.getLogger(RealtimeController.class);

    @MessageMapping("/message")
    public void receiveMessage(@Payload MessageModel messageModel) {
        String destination = "/group/" + messageModel.getChatEntity().getId();
        template.convertAndSend(destination, messageModel);
        logger.info("Received message: {}", messageModel);
    }
}
