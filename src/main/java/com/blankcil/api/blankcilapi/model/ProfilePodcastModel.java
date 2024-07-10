package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePodcastModel implements Serializable {
    private long id;
    private String thumbnail_url;
    private String audio_url;
    private String title;
    private String content;
    private String nickName;
    private int numberOfLikes;
}
