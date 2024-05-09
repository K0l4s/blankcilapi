package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.CommentEntity;
import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.ParentCommentModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.repository.CommentRepository;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import com.blankcil.api.blankcilapi.utils.FFmpegUtil;
import com.blankcil.api.blankcilapi.utils.ImageProcessing;
import org.modelmapper.ModelMapper;
import com.blankcil.api.blankcilapi.model.PodcastModel;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public PodcastModel createPodcast(PodcastModel podcastModel, MultipartFile imageFile, MultipartFile audioFile) throws Exception {
        PodcastEntity podcastEntity = modelMapper.map(podcastModel, PodcastEntity.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        podcastEntity.setUser_podcast(userEntity);
        podcastEntity.setCreateDay(LocalDateTime.now());

        //resize image
        MultipartFile resizedImageBytes = ImageProcessing.resizeImageTo9by16(imageFile);

        //Store video URL
        byte[] videoBytes = FFmpegUtil.combineMultipartFiles(resizedImageBytes, audioFile);
        if (videoBytes.length == 0) {
            throw new Exception("Video không hợp lệ. Vui lòng thử lại với tệp hình ảnh và âm thanh khác.");
        }
        String videoURL = firebaseService.uploadFileToFirebase(videoBytes);
        podcastEntity.setAudio_url(videoURL);

        podcastRepository.save(podcastEntity);
        return modelMapper.map(podcastEntity, PodcastModel.class);
    }

    @Override
    public List<PodcastModel> getAllPodcasts() {
        List<PodcastEntity> podcastEntities = podcastRepository.findAll();
        return podcastEntities.stream()
                .map(podcastEntity -> {
                    PodcastModel podcastModel = modelMapper.map(podcastEntity, PodcastModel.class);
                    podcastModel.setNumberOfComments(podcastEntity.getComments().size());
                    podcastModel.setNumberOfLikes(podcastEntity.getPodcast_likes().size());
                    return podcastModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PodcastModel getPodcast(int id) {
        Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);
        if (podcastEntity.isPresent()) {
            PodcastModel podcastModel = modelMapper.map(podcastEntity.get(), PodcastModel.class);
            podcastModel.setNumberOfLikes(podcastEntity.get().getPodcast_likes().size());
            podcastModel.setNumberOfComments(podcastEntity.get().getComments().size());
            return podcastModel;
        }
        return null;
    }

    @Override
    public List<PodcastModel> getPodcastsByPage(int pageNumber, int pageSize) {
        // Tính toán offset để lấy dữ liệu từ vị trí bắt đầu
        int offset = pageNumber * pageSize;

        // Lấy danh sách podcast từ repository
        List<PodcastEntity> podcastEntities = podcastRepository.findPaginated(offset, pageSize);

        // Ánh xạ và trả về danh sách podcast model
        return podcastEntities.stream()
                .map(podcastEntity -> {
                    PodcastModel podcastModel = modelMapper.map(podcastEntity, PodcastModel.class);
                    podcastModel.setNumberOfComments(podcastEntity.getComments().size());
                    podcastModel.setNumberOfLikes(podcastEntity.getPodcast_likes().size());
                    return podcastModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PodcastModel> getPodcastsByPageWithAuth(int pageNumber, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Tính toán offset để lấy dữ liệu từ vị trí bắt đầu
        int offset = pageNumber * pageSize;

        // Lấy danh sách podcast từ repository
        List<PodcastEntity> podcastEntities = podcastRepository.findPaginated(offset, pageSize);

        // Ánh xạ và trả về danh sách podcast model
        return podcastEntities.stream()
                .map(podcastEntity -> {
                    PodcastModel podcastModel = modelMapper.map(podcastEntity, PodcastModel.class);
                    podcastModel.setNumberOfComments(podcastEntity.getComments().size());
                    podcastModel.setNumberOfLikes(podcastEntity.getPodcast_likes().size());

                    //Kiem tra xem user dang login da like podcast nay chua ?
                    boolean hasLiked = podcastEntity.getPodcast_likes().stream()
                            .anyMatch(podcastLikeEntity -> podcastLikeEntity.getUser_podcast_like().equals(userEntity));
                    podcastModel.setHasLiked(hasLiked);

                    return podcastModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PodcastModel> getPodcastTrending(int pageNumber, int pageSize) {
        // Tính toán offset để lấy dữ liệu từ vị trí bắt đầu
        int offset = pageNumber * pageSize;

        // Lấy danh sách podcast từ repository
        List<PodcastEntity> podcastEntities = podcastRepository.findPaginatedOrderByLikesDesc(offset, pageSize);

        // Ánh xạ và trả về danh sách podcast model
        return podcastEntities.stream()
                .map(podcastEntity -> {
                    PodcastModel podcastModel = modelMapper.map(podcastEntity, PodcastModel.class);
                    podcastModel.setNumberOfComments(podcastEntity.getComments().size());
                    podcastModel.setNumberOfLikes(podcastEntity.getPodcast_likes().size());
                    return podcastModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PodcastModel> getPodcastTrendingWithAuth(int pageNumber, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Tính toán offset để lấy dữ liệu từ vị trí bắt đầu
        int offset = pageNumber * pageSize;

        // Lấy danh sách podcast từ repository
        List<PodcastEntity> podcastEntities = podcastRepository.findPaginatedOrderByLikesDesc(offset, pageSize);

        // Ánh xạ và trả về danh sách podcast model
        return podcastEntities.stream()
                .map(podcastEntity -> {
                    PodcastModel podcastModel = modelMapper.map(podcastEntity, PodcastModel.class);
                    podcastModel.setNumberOfComments(podcastEntity.getComments().size());
                    podcastModel.setNumberOfLikes(podcastEntity.getPodcast_likes().size());

                    //Kiem tra xem user dang login da like podcast nay chua ?
                    boolean hasLiked = podcastEntity.getPodcast_likes().stream()
                            .anyMatch(podcastLikeEntity -> podcastLikeEntity.getUser_podcast_like().equals(userEntity));
                    podcastModel.setHasLiked(hasLiked);

                    return podcastModel;
                })
                .collect(Collectors.toList());
    }
}
