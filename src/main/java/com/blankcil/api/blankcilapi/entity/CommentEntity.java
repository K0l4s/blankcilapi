package com.blankcil.api.blankcilapi.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Name("comment")
@Builder
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String content;

    //Một comment sẽ có nhiều comment reply
    @OneToMany(mappedBy = "commentEntity")
    private List<CommentEntity> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    public CommentEntity commentEntity;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    public UserEntity user_comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="podcast_id")
    public PocastEntity podcast_comment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comment_likes",
    inverseJoinColumns = @JoinColumn(name = "user_id"),
    joinColumns = @JoinColumn(name = "comment_id"))
    public HashSet<UserEntity> user_like_list = new HashSet<>();
}
