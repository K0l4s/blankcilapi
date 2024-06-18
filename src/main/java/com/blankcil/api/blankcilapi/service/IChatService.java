package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.ChatEntity;
import com.blankcil.api.blankcilapi.model.MessageModel;
import com.blankcil.api.blankcilapi.model.request.ChatRenameRequestModel;
import com.blankcil.api.blankcilapi.model.request.SendMessageModal;
import com.blankcil.api.blankcilapi.model.response.ChatResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IChatService {
    ChatResponse createChat(List<Integer> listTarId);

//    ChatEntity addUserToGroup(int userId, int chatId);

    ChatResponse addUserToGroup(List<Integer> listUserid, int chatId);

    ChatEntity removeUserFromGroup(int userId, int chatId);

    String renameGroup(ChatRenameRequestModel chatModel) ;

    List<ChatResponse> findChatsByUser();

    List<MessageModel> findMessageByChatId(int chatId);

    MessageModel sendMessage(SendMessageModal sendMessageModal);

    MessageModel findMessageById(int id);
}
