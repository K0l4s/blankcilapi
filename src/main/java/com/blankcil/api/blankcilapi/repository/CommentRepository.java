package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE CommentEntity c SET c.totalLikes = (SELECT COUNT(cl) FROM CommentLikeEntity cl WHERE cl.comment_like.id = c.id)")
    void updateTotalLikesForComments();

    @Query("SELECT c, COUNT(cl) AS totalLikes " +
            "FROM CommentEntity c " +
            "LEFT JOIN c.comment_likes cl " +
            "WHERE c.podcast_comment.id = :podcastId AND c.parentComment IS NULL " + // Chỉ sắp xếp các bình luận gốc
            "GROUP BY c " +
            "ORDER BY c.timestamp DESC")
    Page<Object[]> getCommentsWithTotalLikesForPodcast(Long podcastId, Pageable pageable);
}

