package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.ResponseModel;
import com.blankcil.api.blankcilapi.model.SearchModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.service.IUserService;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import com.blankcil.api.blankcilapi.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @ModelAttribute UserModel userModel,
            @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        try {
            UserModel updatedUser = userService.updateUser(userModel, avatarImage, coverImage);
            return ResponseEntity.ok().body(new ResponseModel(true, "Profile updated successfully", updatedUser));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to update profile", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseModel> searchByKeyWords(@RequestParam("keyword") String keyword) {
        try {
            SearchModel searchModels = userService.findByKeywords(keyword);
            if(searchModels.getPodcasts().isEmpty() && searchModels.getUsers().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(false, "Not found", null));
            }
            return ResponseEntity.ok().body(new ResponseModel(true, "Found", searchModels));
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
