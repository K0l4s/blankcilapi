package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.MessageModel;
import com.blankcil.api.blankcilapi.model.request.ChatRenameRequestModel;
import com.blankcil.api.blankcilapi.model.request.SendMessageModal;
import com.blankcil.api.blankcilapi.model.response.ChatResponse;
import com.blankcil.api.blankcilapi.service.ChatServiceImpl;
import com.blankcil.api.blankcilapi.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
public class ChatController {
    @Autowired
    private IChatService chatService = new ChatServiceImpl();


    @PostMapping("/")
    private ResponseEntity<ChatResponse> createChat(@RequestBody List<Integer> listTargId){
        return ResponseEntity.ok(chatService.createChat(listTargId));
    }

    @GetMapping("/all")
    private ResponseEntity<List<ChatResponse>> getAllChat(){
        return ResponseEntity.ok(chatService.findChatsByUser());
    }

    @PostMapping("/rename")
    private ResponseEntity<String> renameChat(@RequestBody ChatRenameRequestModel chatRenameRequestModel){
        return ResponseEntity.ok(chatService.renameGroup(chatRenameRequestModel));
    }

    @GetMapping("/chat/{chatId}")
    private ResponseEntity<List<MessageModel>> findMessageByChatId(
            @PathVariable("chatId") int chatId
    ){
        return ResponseEntity.ok(chatService.findMessageByChatId(chatId));
    }

    @PostMapping("/chat")
    private ResponseEntity<MessageModel> addChat(@RequestBody SendMessageModal sendMessageModal){
        return ResponseEntity.ok(chatService.sendMessage(sendMessageModal));

    }
}
