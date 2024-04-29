package com.blankcil.api.blankcilapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
@Builder
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String content;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    public UserEntity user_comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="podcast_id")
    public PodcastEntity podcast_comment;

    @OneToMany(mappedBy = "comment_like", cascade = CascadeType.ALL)
    private List<CommentLikeEntity> comment_likes;

    @OneToMany(mappedBy = "comment_reply", cascade = CascadeType.ALL)
    private List<ReplyEntity> comment_replies;
}
