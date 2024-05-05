package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;

import java.util.List;

public interface ICommentService {
    List<CommentModel> getCommentsForPodcast(int podcastId);
}
