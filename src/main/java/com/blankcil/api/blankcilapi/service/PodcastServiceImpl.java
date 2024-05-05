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

    @Autowired
    private CommentRepository commentRepository;

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
            return modelMapper.map(podcastEntity.get(), PodcastModel.class);
        }
        return null;
    }

    @Override
    public List<CommentModel> getCommentsForPodcast(int podcastId) {
        // Cập nhật totalLikes cho tất cả các bình luận trước khi truy vấn danh sách bình luận
        commentRepository.updateTotalLikesForComments();

        List<Object[]> commentObjects = commentRepository.getCommentsWithTotalLikesForPodcast((long) podcastId);

        // Chuyển đổi danh sách các đối tượng Object[] thành danh sách các CommentModel
        List<CommentModel> comments = commentObjects.stream()
                .map(object -> {
                    CommentEntity commentEntity = (CommentEntity) object[0];
                    Long totalLikes = (Long) object[1];

                    // Cập nhật totalLikes cho mỗi CommentEntity
                    commentEntity.setTotalLikes(totalLikes);

                    // Cập nhật totalLikes cho các bình luận trong replies
                    List<CommentEntity> replies = commentEntity.getReplies();
                    for (CommentEntity reply : replies) {
                        long replyTotalLikes = reply.getComment_likes().size();
                        reply.setTotalLikes(replyTotalLikes);

                        // Ánh xạ thông tin user_comment cho parentComment của reply
                        if (reply.getParentComment() != null) {
                            ParentCommentModel parentCommentModel = modelMapper.map(reply.getParentComment(), ParentCommentModel.class);
                            parentCommentModel.setUser_comment(modelMapper.map(reply.getParentComment().getUser_comment(), UserModel.class));
                        }
                    }

                    // Tiếp tục ánh xạ các thông tin còn lại từ CommentEntity sang CommentModel
                    CommentModel commentModel = modelMapper.map(commentEntity, CommentModel.class);

                    // Tiếp tục ánh xạ dữ liệu user_comment cho parentComment nếu có
                    if (commentEntity.getParentComment() != null) {
                        ParentCommentModel parentCommentModel = modelMapper.map(commentEntity.getParentComment(), ParentCommentModel.class);
                        parentCommentModel.setUser_comment(modelMapper.map(commentEntity.getParentComment().getUser_comment(), UserModel.class));
                        commentModel.setParentComment(parentCommentModel);
                    }

                    return commentModel;
                })
                .collect(Collectors.toList());

        return comments;
    }
}
