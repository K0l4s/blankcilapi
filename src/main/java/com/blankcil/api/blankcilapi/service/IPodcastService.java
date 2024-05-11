package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPodcastService {
    PodcastModel createPodcast(PodcastModel podcastModel, MultipartFile imageFile, MultipartFile audioFile) throws Exception;
    List<PodcastModel> getAllPodcasts();
    PodcastModel getPodcast(int id);
    List<PodcastModel> getPodcastsByPage(int pageNumber, int pageSize);
    List<PodcastModel> getPodcastsByPageWithAuth(int pageNumber, int pageSize) throws IOException;

    List<PodcastModel> getPodcastTrending(int pageNumber, int pageSize);
    List<PodcastModel> getPodcastTrendingWithAuth(int pageNumber, int pageSize);

}
