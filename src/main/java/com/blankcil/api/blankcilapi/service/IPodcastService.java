package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.PodcastModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPodcastService {
    PodcastModel createPodcast(PodcastModel podcastModel, MultipartFile imageFile, MultipartFile audioFile) throws IOException, InterruptedException;
}
