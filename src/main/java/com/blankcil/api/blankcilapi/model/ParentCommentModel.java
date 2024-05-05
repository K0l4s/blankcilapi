package com.blankcil.api.blankcilapi.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentCommentModel implements Serializable {
    private long id;
    private String content;
    private LocalDateTime timestamp;
    private UserModel user_comment;
    private int totalLikes;
    private ParentCommentModel parentComment;

}