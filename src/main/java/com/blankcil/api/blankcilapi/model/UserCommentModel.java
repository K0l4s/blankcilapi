package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentModel implements Serializable {
    private Integer id;
    private String fullname;
    private String avatar_url;
    private String email;
    private String nickName;
    private int followers;
    private int following;
//    private LocalDate birthday;
//    private String address;
//    private String phone;
}
