package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.ReplyCommentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {
//    List<CommentModel> getCommentsForPodcast(int podcastId);
    Page<CommentModel> getCommentsForPodcast(int podcastId, Pageable pageable);
    Page<CommentModel> getCommentsForPodcastWithAuth(int podcastId, Pageable pageable);
    List<ReplyCommentModel> getReplies(long commentId);
}
