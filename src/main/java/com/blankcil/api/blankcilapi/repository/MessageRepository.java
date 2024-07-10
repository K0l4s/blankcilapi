package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity,Integer> {
    public List<MessageEntity> findMessageEntitiesByChatEntityIdOrderByTimestampAsc(int chatId);
    MessageEntity findChatEntityById(int id);
}
