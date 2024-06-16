package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.ChatEntity;
import com.blankcil.api.blankcilapi.entity.MessageEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.model.MessageModel;
import com.blankcil.api.blankcilapi.model.request.ChatRenameRequestModel;
import com.blankcil.api.blankcilapi.model.request.SendMessageModal;
import com.blankcil.api.blankcilapi.model.response.ChatResponse;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.repository.ChatRepository;
import com.blankcil.api.blankcilapi.repository.MessageRepository;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements IChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ChatResponse createChat(List<Integer> listTarId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity reqUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        ChatEntity chat = new ChatEntity();
        List<UserEntity> listUsers = userRepository.findAllByIdIn(listTarId);
        listUsers.add(reqUser);
        chat.setCreatedBy(reqUser);
        chat.setMembers(new HashSet<>(listUsers));
        if (listUsers.size() > 2) {
            chat.setGroup(true);
        }
        chat.setTimeStamp(LocalDateTime.now());
        StringBuilder title = new StringBuilder();
        for (UserEntity user : listUsers) {
            title.append(user.getUsername()).append(", ");
        }
        if (title.length() > 0) {
            title.setLength(title.length() - 2);
        }
        chat.setTitle(title.toString());
        ChatEntity savedChat = chatRepository.save(chat);
        ChatResponse chatResponse = modelMapper.map(savedChat, ChatResponse.class);

        for (UserEntity user : listUsers) {
            chatResponse.getMembers().add(modelMapper.map(user, UserModel.class));
        }
        chatResponse.setCreatedBy(modelMapper.map(savedChat.getCreatedBy(), UserModel.class));
        return chatResponse;
    }


    @Override
    public ChatResponse addUserToGroup(List<Integer> listUserid, int chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        if (!chatRepository.existsByIdAndMembersContains(chatId, userEntity)) {
            throw new RuntimeException("Don't have permission to rename group");
        }
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        List<UserEntity> newUsers = userRepository.findAllByIdIn(listUserid);

        ChatEntity savedChat = chatRepository.save(chat);
        ChatResponse chatResponse = modelMapper.map(savedChat, ChatResponse.class);

        for (UserEntity user : savedChat.getMembers()) {
            chatResponse.getMembers().add(modelMapper.map(user, UserModel.class));
        }
        chatResponse.setCreatedBy(modelMapper.map(savedChat.getCreatedBy(), UserModel.class));
        return chatResponse;
    }

    @Override
    public ChatEntity removeUserFromGroup(int userId, int chatId) {
        return null;
    }

    @Override
    public String renameGroup(ChatRenameRequestModel chatModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        if (!chatRepository.existsByIdAndMembersContains(chatModel.getChatId(), userEntity)) {
            throw new RuntimeException("Don't have permission to rename group");
        }

        ChatEntity chat = chatRepository.findById(chatModel.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        chat.setTitle(chatModel.getName());
        ChatEntity saved = chatRepository.save(chat);
        return saved.getTitle();
    }


    @Override
    public List<ChatResponse> findChatsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        List<ChatEntity> chatEntityList = chatRepository.findChatEntitiesByMembersContains(userEntity);

        return chatEntityList.stream().map(chatEntity -> {
            ChatResponse chatResponse = modelMapper.map(chatEntity, ChatResponse.class);
            List<UserModel> membersList = chatResponse.getMembers();
            chatEntity.getMembers().forEach(user -> {
                UserModel userModel = modelMapper.map(user, UserModel.class);
                if (!membersList.contains(userModel)) {
                    membersList.add(userModel);
                }
            });
            chatResponse.setCreatedBy(modelMapper.map(chatEntity.getCreatedBy(), UserModel.class));
            return chatResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MessageModel> findMessageByChatId(int chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        List<MessageEntity> listMessages = messageRepository.findMessageEntitiesByChatEntityIdOrderByTimestampAsc(chatId);
        List<MessageModel> messageModelList = listMessages.stream()
                .map(messageEntity -> {
                    MessageModel messageModel = modelMapper.map(messageEntity, MessageModel.class);
//                    UserModel userModel = modelMapper.map(messageEntity.getUser(), UserModel.class);
//                    messageModel.setUser(userModel);
                    return messageModel;
                })
                .collect(Collectors.toList());

        return messageModelList;
    }
    @Override
    public MessageModel sendMessage(SendMessageModal sendMessageModal){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setChatEntity(chatRepository.findById(sendMessageModal.getChatId()).orElseThrow());
        messageEntity.setUser(userEntity);
        messageEntity.setTimestamp(LocalDateTime.now());
        messageEntity.setContent(sendMessageModal.getContent());
        MessageEntity savedMessageEntity = messageRepository.save(messageEntity);
        return modelMapper.map(savedMessageEntity,MessageModel.class);
    }


}
