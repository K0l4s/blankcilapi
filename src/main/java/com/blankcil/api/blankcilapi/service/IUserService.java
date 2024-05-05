package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;

import java.security.Principal;

public interface IUserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    UserModel getProfile();
    UserModel updateUser(UserModel userModel);
}
