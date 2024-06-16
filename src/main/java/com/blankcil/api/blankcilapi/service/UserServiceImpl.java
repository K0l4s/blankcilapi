package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.*;
import com.blankcil.api.blankcilapi.model.*;
import com.blankcil.api.blankcilapi.repository.*;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private PodcastLikeRepository podcastLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IFirebaseService firebaseService;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }

    @Override
    public UserModel getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Lấy danh sách podcast của user
        List<ProfilePodcastModel> profilePodcasts = new ArrayList<>();
        for (PodcastEntity podcastEntity : userEntity.getPodcasts()) {
            ProfilePodcastModel profilePodcastModel = modelMapper.map(podcastEntity, ProfilePodcastModel.class);
            // Lấy số lượng likes cho podcast
            int numberOfLikes = podcastEntity.getPodcast_likes().size();
            profilePodcastModel.setNumberOfLikes(numberOfLikes);
            profilePodcasts.add(profilePodcastModel);
        }

        // Tạo UserModel và set danh sách podcasts
        UserModel userModel = modelMapper.map(userEntity, UserModel.class);
        userModel.setPodcasts(profilePodcasts);

        return userModel;
    }
    @Override
    public UserModel getProfileOtherByNickname(String nickname) {

        UserEntity userEntity = userRepository.findUserEntityByNickName(nickname)
                .orElseThrow(() -> new RuntimeException("User not found with Nickname: " + nickname));

        // Lấy danh sách podcast của user
        List<ProfilePodcastModel> profilePodcasts = new ArrayList<>();
        for (PodcastEntity podcastEntity : userEntity.getPodcasts()) {
            ProfilePodcastModel profilePodcastModel = modelMapper.map(podcastEntity, ProfilePodcastModel.class);
            // Lấy số lượng likes cho podcast
            int numberOfLikes = podcastEntity.getPodcast_likes().size();
            profilePodcastModel.setNumberOfLikes(numberOfLikes);
            profilePodcasts.add(profilePodcastModel);
        }

        // Tạo UserModel và set danh sách podcasts
        UserModel userModel = modelMapper.map(userEntity, UserModel.class);
        userModel.setPodcasts(profilePodcasts);
        userModel.setFollowers(followRepository.countByTargetId(userEntity.getId()));
        userModel.setFollowing(followRepository.countByFollowerId(userEntity.getId()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {

            String userEmail = authentication.getName();
            if(!userEmail.equals("anonymousUser")){
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));;
            if(followRepository.existsByFollowerIdAndTargetId(user.getId(), userEntity.getId())) {
                userModel.setFollow(true);
            }
        }

        return userModel;
    }

    @Override
    public UserModel updateUser(UserModel userModel, MultipartFile avatarImage, MultipartFile coverImage) throws IOException {
        Principal connectedUser = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = connectedUser.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        firebaseService.createUserFolder(userEntity.getId().toString(), userEmail);

        // Cập nhật thông tin người dùng nếu có sự thay đổi
        if (userModel.getFullname() != null) {
            userEntity.setFullname(userModel.getFullname());
        }
        if (userModel.getBirthday() != null) {
            userEntity.setBirthday(userModel.getBirthday());
        }
        if (userModel.getAddress() != null) {
            userEntity.setAddress(userModel.getAddress());
        }
        if (userModel.getPhone() != null) {
            userEntity.setPhone(userModel.getPhone());
        }
        if (avatarImage != null) {
            firebaseService.deleteFileFromFirebase(userEntity.getAvatar_url());
            String avatarUrl = firebaseService.uploadImageToFirebase(avatarImage, "avatar");
            userEntity.setAvatar_url(avatarUrl);
        }
        if (coverImage != null) {
            firebaseService.deleteFileFromFirebase(userEntity.getCover_url());
            String coverUrl = firebaseService.uploadImageToFirebase(coverImage, "cover");
            userEntity.setCover_url(coverUrl);
        }

        userRepository.save(userEntity);

        // Trả về thông tin người dùng sau khi cập nhật
        return modelMapper.map(userEntity, UserModel.class);
    }

    @Override
    public SearchModel findByKeywords(String keyword) {
        List<UserEntity> userEntities = userRepository.findByFullnameIgnoreCaseContaining(keyword);
        List<PodcastEntity> podcastEntities = podcastRepository.findByTitleIgnoreCaseContainingOrContentIgnoreCaseContaining(keyword, keyword);
        List<UserModel> userModels = userEntities.stream().map(userEntity -> modelMapper.map(userEntity,UserModel.class)).toList();
        List<PodcastModel> podcastModels = podcastEntities.stream().map(podcastEntity -> modelMapper.map(podcastEntity,PodcastModel.class)).toList();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
        {
            String userEmail = authentication.getName();
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            for(UserModel targetUser : userModels) {
                if(followRepository.existsByFollowerIdAndTargetId(user.getId(), targetUser.getId()))
                    targetUser.setFollow(true);
            }
        }
        return new SearchModel(userModels, podcastModels);
    }

    @Override
    public String likePodcast(int podcastId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PodcastEntity podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() -> new RuntimeException("Podcast not found"));

        // Kiểm tra xem user đã like bài viết này chưa
        PodcastLikeEntity existingLike = findExistingLike(user, podcast);
        if (existingLike != null) {
            podcastLikeRepository.delete(existingLike);
            return "Unliked";
        } else {
            PodcastLikeEntity like = PodcastLikeEntity.builder()
                    .timestamp(LocalDateTime.now())
                    .user_podcast_like(user)
                    .podcast_like(podcast)
                    .build();

            podcastLikeRepository.save(like);
            return "Liked";
        }
    }

    private PodcastLikeEntity findExistingLike(UserEntity user, PodcastEntity podcast) {
        // Tìm like của user cho podcast này
        for (PodcastLikeEntity like : user.getPodcast_likes()) {
            if (like.getPodcast_like().equals(podcast)) {
                return like;
            }
        }
        return null;
    }

    @Override
    public CommentModel commentOnPodcast(String content, int podcastId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PodcastEntity podcastEntity = podcastRepository.findById(podcastId)
                .orElseThrow(() -> new RuntimeException("Podcast not found"));

        CommentEntity commentEntity = CommentEntity.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .podcast_comment(podcastEntity)
                .user_comment(userEntity)
                .totalLikes(0)
                .totalReplies(0)
                .build();
        commentRepository.save(commentEntity);
        return modelMapper.map(commentEntity, CommentModel.class);
    }

    @Override
    public CommentModel replyComment(String content, long parentCommentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity commentEntity = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        PodcastEntity podcastEntity = commentEntity.getPodcast_comment();

        CommentEntity replyComment = CommentEntity.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .parentComment(commentEntity)
                .podcast_comment(podcastEntity)
                .user_comment(userEntity)
                .totalLikes(0)
                .totalReplies(0)
                .build();
        commentRepository.save(replyComment);
        return modelMapper.map(replyComment, CommentModel.class);
    }

    @Override
    public String likeComment(long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        // Kiểm tra xem user đã like comment này chưa ?
        CommentLikeEntity existingCommentLike = findExistingCommentLike(user, comment);
        // Nếu comment đã tồn tại (đã được user like trước đó) thì xóa like đó đi
        if (existingCommentLike != null) {
            commentLikeRepository.delete(existingCommentLike);
            return "Unliked";
        } else {
            CommentLikeEntity like = CommentLikeEntity.builder()
                    .timestamp(LocalDateTime.now())
                    .user_comment_like(user).comment_like(comment)
                    .build();
            commentLikeRepository.save(like);
            return "Liked";
        }
    }

    private CommentLikeEntity findExistingCommentLike(UserEntity user, CommentEntity comment) {
        // Tìm like của user cho podcast này
        for (CommentLikeEntity like : user.getComment_likes()) {
            if (like.getComment_like().equals(comment)) {
                return like;
            }
        }
        return null;
    }

}
