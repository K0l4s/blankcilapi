package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.ResponseModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.service.IUserService;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import com.blankcil.api.blankcilapi.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    IUserService userService = new UserServiceImpl();

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseModel> getProfile() {
        try {
            UserModel userModel = userService.getProfile();
            return ResponseEntity.ok().body(new ResponseModel(true, "Get profile successfully", userModel));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to get profile", null));
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ResponseModel> getProfileOther(@PathVariable int id) {
        try {
            UserModel userModel = userService.getProfileOther(id);
            return ResponseEntity.ok().body(new ResponseModel(true, "Get profile successfully", userModel));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to get profile", null));
        }
    }



    @PutMapping("/profile/edit")
    public ResponseEntity<ResponseModel> updateProfile(
            @RequestBody UserModel userModel
    ) {
        try {
            UserModel updatedUser = userService.updateUser(userModel);
            return ResponseEntity.ok().body(new ResponseModel(true, "Profile updated successfully", updatedUser));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to update profile", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseModel> searchUsersByFullname(@RequestParam("name") String fullname) {
        try {
            List<UserModel> users = userService.findUsersByFullname(fullname);
            if(users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(false, "User not found", null));
            }
            return ResponseEntity.ok().body(new ResponseModel(true, "Found", users));
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Error", null));
        }
    }

    @PostMapping("/like/podcast/{id}")
    public ResponseEntity<ResponseModel> likePodcast(@PathVariable("id") int id) {
        try {
            String msg = userService.likePodcast(id);
            return ResponseEntity.ok().body(new ResponseModel(true, msg, null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Error", null));
        }
    }

    @PostMapping("/comment/podcast")
    public ResponseEntity<ResponseModel> commentOnPodcast(@RequestParam("content") String content,
                                                          @RequestParam("podcastId") int podcastId) {
        try {
            CommentModel comment = userService.commentOnPodcast(content, podcastId);
            return ResponseEntity.ok().body(new ResponseModel(true, "Comment successfully", comment));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed!!", null));
        }
    }

    @PostMapping("/reply/comment")
    public ResponseEntity<ResponseModel> replyOnComment(@RequestParam("content") String content,
                                                          @RequestParam("commentId") int commentId) {
        try {
            CommentModel comment = userService.replyComment(content, commentId);
            return ResponseEntity.ok().body(new ResponseModel(true, "Reply comment successfully", comment));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed!!", null));
        }
    }
}
