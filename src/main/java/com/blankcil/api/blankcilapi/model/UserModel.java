package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel implements Serializable {
    private Integer id;
    private String fullname;
    private String avatar_url;
    private String cover_url;
    private String email;
    private LocalDate birthday;
    private String address;
    private String phone;
    private String nickName;
    private boolean isFollow = false;
    private int followers;
    private int following;
    private List<ProfilePodcastModel> podcasts;
}
