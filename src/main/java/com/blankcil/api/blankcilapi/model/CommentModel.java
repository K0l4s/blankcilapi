package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel implements Serializable {
    private long id;
    private String content;
    private LocalDateTime timestamp;
    private UserModel user_comment;
    private PodcastModel podcast_comment;
    private List<CommentLikeModel> comment_likes;
    private CommentModel parentComment;
    private List<CommentLikeModel> replies;
}
