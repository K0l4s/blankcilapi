package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE CommentEntity c SET c.totalLikes = (SELECT COUNT(cl) FROM CommentLikeEntity cl WHERE cl.comment_like.id = c.id)")
    void updateTotalLikesForComments();

    // Phương thức truy vấn JPQL để đếm số lượt thích cho mỗi bình luận và trả về danh sách các CommentEntity đã được cập nhật với totalLikes
    @Query("SELECT c, (SELECT COUNT(cl) FROM CommentLikeEntity cl WHERE cl.comment_like.id = c.id) AS totalLikes FROM CommentEntity c WHERE c.podcast_comment.id = :podcastId")
    List<Object[]> getCommentsWithTotalLikesForPodcast(Long podcastId);
}

