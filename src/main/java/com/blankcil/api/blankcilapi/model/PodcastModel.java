package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodcastModel implements Serializable {
    private long id;
    private String title;
    private String audio_url;
    private String content;
    private LocalDateTime createDay;
    private UserModel user_podcast;
    private List<CommentModel> comments;
    private List<PodcastLikeModel> podcast_likes;
}
