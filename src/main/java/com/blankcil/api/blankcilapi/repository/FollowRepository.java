package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.FollowEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity,Integer> {
    List<FollowEntity> findByFollowerId(int followerId);
    List<FollowEntity> findByTargetId(int targetId);
    FollowEntity findByFollowerIdAndTargetId(int followerId, int targetId);
//    List<FollowEntity> saveAll(List<FollowEntity> followers);
    boolean existsByFollowerIdAndTargetId(int followerId, int targetId);
//    void saveByFollowerIdAndTargetId(int followerid,int targetid);
    int countByFollowerId(int followerId);
    int countByTargetId(int targetId);
}
