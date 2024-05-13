package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Integer> {
}
