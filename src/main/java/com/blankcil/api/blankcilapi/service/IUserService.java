package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.SearchModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface IUserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    UserModel getProfile();
    UserModel getProfileOther(int id);
    UserModel updateUser(UserModel userModel, MultipartFile avatarImage, MultipartFile coverImage) throws IOException;
    SearchModel findByKeywords(String keyword);
    String likePodcast(int podcastId);
    CommentModel commentOnPodcast(String content, int podcastId);
    CommentModel replyComment(String content, long parentCommentId);
}
