package com.blankcil.api.blankcilapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="chat")
public class ChatEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String title;
    private String image;

    private LocalDateTime timeStamp;

    private boolean isGroup =false;

    @JoinColumn(name="created_by")
    @ManyToOne
    private UserEntity createdBy;

    @ManyToMany
    private Set<UserEntity> members = new HashSet<>();

//    @OneToMany
//    private List<MessageEntity> messages = new ArrayList<>();
@OneToMany(mappedBy = "chatEntity", cascade = CascadeType.ALL, orphanRemoval = true)
private List<MessageEntity> messages = new ArrayList<>();
}
