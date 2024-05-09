package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;

import java.security.Principal;
import java.util.List;

public interface IUserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    UserModel getProfile();
    UserModel getProfileOther(int id);
    UserModel updateUser(UserModel userModel);
    List<UserModel> findUsersByFullname(String fullname);
    String likePodcast(int podcastId);
    CommentModel commentOnPodcast(String content, int podcastId);
}
