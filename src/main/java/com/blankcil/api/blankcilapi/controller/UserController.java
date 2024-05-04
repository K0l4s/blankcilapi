package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.ResponseModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import com.blankcil.api.blankcilapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponseModel> getProfile() {
        try {
            UserModel userModel = service.getProfile();
            return ResponseEntity.ok().body(new ResponseModel(true, "Get profile successfully", userModel));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to get profile", null));
        }
    }
}
