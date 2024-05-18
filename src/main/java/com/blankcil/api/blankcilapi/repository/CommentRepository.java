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

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parentComment.id = :parentId")
    long countRepliesByParentCommentId(long parentId);

    @Query("SELECT c, COUNT(DISTINCT cl.id) AS totalLikes, COUNT(DISTINCT r.id) AS totalReplies " +
            "FROM CommentEntity c " +
            "LEFT JOIN c.comment_likes cl " +
            "LEFT JOIN c.replies r " +
            "WHERE c.podcast_comment.id = :podcastId AND c.parentComment IS NULL " +
            "GROUP BY c.id " +
            "ORDER BY c.timestamp DESC")
    Page<Object[]> getCommentsWithTotalLikesAndTotalRepliesForPodcast(Long podcastId, Pageable pageable);
}

