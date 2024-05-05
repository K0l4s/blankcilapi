package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import com.blankcil.api.blankcilapi.model.ResponseModel;
import com.blankcil.api.blankcilapi.service.CommentServiceImpl;
import com.blankcil.api.blankcilapi.service.ICommentService;
import com.blankcil.api.blankcilapi.service.IPodcastService;
import com.blankcil.api.blankcilapi.service.PodcastServiceImpl;
import com.blankcil.api.blankcilapi.utils.MultipartFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/podcast")
public class PodcastController {
    @Autowired
    IPodcastService podcastService = new PodcastServiceImpl();

    @Autowired
    ICommentService commentService = new CommentServiceImpl();

    @PostMapping("/upload")
    public ResponseEntity<ResponseModel>createPodcast(@ModelAttribute PodcastModel podcastModel,
                                                      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                                      @RequestParam("audioFile") MultipartFile audioFile) {
        MultipartFile fileToUpload;
        if (imageFile == null || imageFile.isEmpty()) {
            fileToUpload = MultipartFileUtils.createDefaultImage();
        }
        else {
            fileToUpload = imageFile;
        }
        try {
            PodcastModel createdPodcast = podcastService.createPodcast(podcastModel, fileToUpload, audioFile);
            return ResponseEntity.ok(new ResponseModel(true, "Podcast created successfully", createdPodcast));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to create podcast", null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseModel> getAllPodcasts() {
        try {
            List<PodcastModel> podcasts = podcastService.getAllPodcasts();
            return ResponseEntity.ok(new ResponseModel(true, "Get successfully", podcasts));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getPodcast(@PathVariable int id) {
        try {
            PodcastModel podcasts = podcastService.getPodcast(id);
            return ResponseEntity.ok(new ResponseModel(true, "Get successfully", podcasts));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed", null));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseModel> getCommentsForPodcast(@PathVariable int id) {
        try {
            List<CommentModel> comments = commentService.getCommentsForPodcast(id);
            return ResponseEntity.ok(new ResponseModel(true, "Get comments successfully", comments));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(false, "Failed to get comments", null));
        }
    }

}
