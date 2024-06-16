package com.blankcil.api.blankcilapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRenameRequestModel {
    private String name;
    private int chatId;
}
