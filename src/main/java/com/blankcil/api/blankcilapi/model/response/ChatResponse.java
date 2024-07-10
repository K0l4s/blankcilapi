package com.blankcil.api.blankcilapi.model.response;

import com.blankcil.api.blankcilapi.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private int id;
    private String title;
    private String image;
    private LocalDateTime timeStamp;
    private boolean isGroup;
    private UserModel createdBy;
    private List<UserModel> members = new ArrayList<>();
}
