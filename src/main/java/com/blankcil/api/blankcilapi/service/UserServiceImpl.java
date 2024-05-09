package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import com.blankcil.api.blankcilapi.entity.PodcastLikeEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.model.ProfilePodcastModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.repository.PodcastLikeRepository;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import com.blankcil.api.blankcilapi.user.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserModel getProfileOther(int id) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

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
    public UserModel updateUser(UserModel userModel) {
        Principal connectedUser = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = connectedUser.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

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

        userRepository.save(userEntity);

        // Trả về thông tin người dùng sau khi cập nhật
        return modelMapper.map(userEntity, UserModel.class);
    }

    @Override
    public List<UserModel> findUsersByFullname(String fullname) {
        List<UserEntity> userEntities = userRepository.findByFullnameIgnoreCaseContaining(fullname);
        return userEntities.stream()
                .map(userEntity -> modelMapper.map(userEntity,UserModel.class))
                .toList();
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
}
