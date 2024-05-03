package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import com.blankcil.api.blankcilapi.utils.FFmpegUtil;
import org.modelmapper.ModelMapper;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PodcastServiceImpl implements IPodcastService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IFirebaseService firebaseService;

    @Override
    public PodcastModel createPodcast(PodcastModel podcastModel, MultipartFile imageFile, MultipartFile audioFile) throws IOException, InterruptedException {
        PodcastEntity podcastEntity = modelMapper.map(podcastModel, PodcastEntity.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        podcastEntity.setUser_podcast(userEntity);
        podcastEntity.setCreateDay(LocalDateTime.now());

        //Store video URL
        byte[] videoBytes = FFmpegUtil.combineMultipartFiles(imageFile, audioFile);
        String videoURL = firebaseService.uploadFileToFirebase(videoBytes);
        podcastEntity.setAudio_url(videoURL);

        podcastRepository.save(podcastEntity);
        return modelMapper.map(podcastEntity, PodcastModel.class);
    }

    @Override
    public List<PodcastModel> getAllPodcasts() {
        List<PodcastEntity> podcastEntities = podcastRepository.findAll();
        return podcastEntities.stream()
                .map(podcastEntity -> modelMapper.map(podcastEntity, PodcastModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public PodcastModel getPodcast(int id) {
        Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);
        if (podcastEntity.isPresent()) {
            return modelMapper.map(podcastEntity, PodcastModel.class);
        }
        return null;
    }
}
