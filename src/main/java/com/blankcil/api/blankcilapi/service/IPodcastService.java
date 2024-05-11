package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.PageResponse;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPodcastService {
    PodcastModel createPodcast(PodcastModel podcastModel, MultipartFile imageFile, MultipartFile audioFile) throws Exception;
    List<PodcastModel> getAllPodcasts();
    PodcastModel getPodcast(int id);
    PageResponse<PodcastModel> getPodcastsByPage(int pageNumber, int pageSize);
    PageResponse<PodcastModel> getPodcastsByPageWithAuth(int pageNumber, int pageSize) throws IOException;

    PageResponse<PodcastModel> getPodcastTrending(int pageNumber, int pageSize);
    PageResponse<PodcastModel> getPodcastTrendingWithAuth(int pageNumber, int pageSize);

}
