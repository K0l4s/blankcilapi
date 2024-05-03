package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodcastLikeModel implements Serializable {
    private Long id;
    private LocalDateTime timestamp;
    private PodcastModel podcast_like;
    private UserModel user_podcast_like;
}
