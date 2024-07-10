package com.blankcil.api.blankcilapi.model;

import com.blankcil.api.blankcilapi.model.response.ChatResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageModel {
    private int id;
    private String content;
    private LocalDateTime timestamp;
    private UserModel user;
    private ChatResponse chatEntity;
}
