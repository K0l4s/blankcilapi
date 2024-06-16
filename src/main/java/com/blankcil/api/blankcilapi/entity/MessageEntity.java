package com.blankcil.api.blankcilapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="message")
public class MessageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="chat_id")
    private ChatEntity chatEntity;
}
