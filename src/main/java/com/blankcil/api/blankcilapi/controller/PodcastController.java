package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.PodcastModel;
import com.blankcil.api.blankcilapi.model.ResponseModel;
import com.blankcil.api.blankcilapi.service.IPodcastService;
import com.blankcil.api.blankcilapi.service.PodcastServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/podcast")
public class PodcastController {
    @Autowired
    IPodcastService podcastService = new PodcastServiceImpl();

    @PostMapping("/upload")
    public ResponseEntity<ResponseModel>createPodcast(@ModelAttribute PodcastModel podcastModel) {
        try {
            PodcastModel createdPodcast = podcastService.createPodcast(podcastModel);
            return ResponseEntity.ok(new ResponseModel(true, "Podcast created successfully", createdPodcast));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel(false, "Failed to create podcast", null));
        }
    }
}
