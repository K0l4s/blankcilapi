package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PodcastServiceImpl implements IPodcastService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PodcastModel createPodcast(PodcastModel podcastModel) {
        PodcastEntity podcastEntity = modelMapper.map(podcastModel, PodcastEntity.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        podcastEntity.setUser_podcast(userEntity);
        podcastEntity.setCreateDay(LocalDateTime.now());

        podcastRepository.save(podcastEntity);
        return modelMapper.map(podcastEntity, PodcastModel.class);
    }
}
