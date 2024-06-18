package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.ChatEntity;
import com.blankcil.api.blankcilapi.entity.MessageEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,Integer> {
    List<ChatEntity> findChatEntitiesByMembersContains(UserEntity user);
//    boolean existsChatEntityByMembersContains(UserEntity user);
    boolean existsByIdAndMembersContains(int id,UserEntity user);

}
