package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.*;
import com.blankcil.api.blankcilapi.model.response.PageResponse;
import com.blankcil.api.blankcilapi.model.response.ResponseModel;
import com.blankcil.api.blankcilapi.service.CommentServiceImpl;
import com.blankcil.api.blankcilapi.service.ICommentService;
import com.blankcil.api.blankcilapi.service.IPodcastService;
import com.blankcil.api.blankcilapi.service.PodcastServiceImpl;
import com.blankcil.api.blankcilapi.utils.MultipartFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @CrossOrigin
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

    @GetMapping("/view/{id}")
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

    @GetMapping("auth/view/{id}")
    public ResponseEntity<ResponseModel> getPodcastWithAuth(@PathVariable int id) {
        try {
            PodcastModel podcasts = podcastService.getPodcastWithAuth(id);
            return ResponseEntity.ok(new ResponseModel(true, "Get successfully", podcasts));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed", null));
        }
    }

    @GetMapping("/view/page")
    public ResponseEntity<ResponseModel> getPodcastsByPage(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "trending", defaultValue = "false") boolean trending)
    {
        try {
            PageResponse<PodcastModel> pageResponse;
            if (trending) {
                pageResponse = podcastService.getPodcastTrending(pageNumber, pageSize);
            } else {
                pageResponse = podcastService.getPodcastsByPage(pageNumber, pageSize);
            }
            return ResponseEntity.ok(new ResponseModel(true, "Get successfully", pageResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed", null));
        }
    }

    @GetMapping("/auth/view/page")
    public ResponseEntity<ResponseModel> getPodcastsByPageWithAuth(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "trending", defaultValue = "false") boolean trending)
    {
        try {
            PageResponse<PodcastModel> pageResponse;
            if (trending) {
                pageResponse = podcastService.getPodcastTrendingWithAuth(pageNumber, pageSize);
            } else {
                pageResponse = podcastService.getPodcastsByPageWithAuth(pageNumber, pageSize);
            }
            return ResponseEntity.ok(new ResponseModel(true, "Get successfully", pageResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed", null));
        }
    }

    @GetMapping("/view/{id}/comments")
    public ResponseEntity<ResponseModel> getCommentsForPodcast(@PathVariable int id,
                                                               @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CommentModel> commentPage = commentService.getCommentsForPodcast(id, pageable);
            return ResponseEntity.ok(new ResponseModel(true, "Get comments successfully", commentPage.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(false, "Failed to get comments", null));
        }
    }

    @GetMapping("/auth/view/{id}/comments")
    public ResponseEntity<ResponseModel> getCommentsForPodcastWithAuth(@PathVariable int id,
                                                               @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CommentModel> commentPage = commentService.getCommentsForPodcastWithAuth(id, pageable);
            return ResponseEntity.ok(new ResponseModel(true, "Get comments successfully", commentPage.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(false, "Failed to get comments", null));
        }
    }

    @GetMapping("/view/replies/{id}")
    public ResponseEntity<ResponseModel> getReplies(@PathVariable long id) {
        try {
            List<ReplyCommentModel> replies = commentService.getReplies(id);
            return ResponseEntity.ok(new ResponseModel(true, "Get replies successfully", replies));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to get comment", null));
        }
    }
}
