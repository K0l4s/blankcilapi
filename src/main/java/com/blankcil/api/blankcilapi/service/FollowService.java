package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.FollowEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.repository.FollowRepository;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    public String follow(int targetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        FollowEntity exist = followRepository.findByFollowerIdAndTargetId(userEntity.getId(),targetId);
        if(exist != null) {
            followRepository.delete(exist);
            return "isUnfollow";
        }
        FollowEntity followEntity = new FollowEntity();
        followEntity.setFollower(userEntity);
        followEntity.setTarget(userRepository.findById(targetId).orElseThrow());
        followRepository.save(followEntity);
        FollowEntity saved = followRepository.save(followEntity);
        return "isFollow";
    }
}
