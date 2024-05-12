package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PodcastRepository extends JpaRepository<PodcastEntity, Integer> {
    @Query(value = "SELECT * FROM Podcast ORDER BY create_day DESC LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<PodcastEntity> findPaginated(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query(value = "SELECT p.*, COUNT(pl.id) AS like_count " +
            "FROM podcast p " +
            "LEFT JOIN podcast_like pl ON p.id = pl.podcast_id " +
            "GROUP BY p.id, p.title, p.audio_url, p.content, p.create_day, p.user_id " +
            "ORDER BY like_count DESC, p.create_day DESC " + // Sắp xếp theo số lượt thích giảm dần, sau đó theo ngày tạo giảm dần
            "LIMIT :pageSize OFFSET :offset",
            nativeQuery = true)
    List<PodcastEntity> findPaginatedOrderByLikesDesc(@Param("offset") int offset, @Param("pageSize") int pageSize);
    List<PodcastEntity> findByTitleIgnoreCaseContainingOrContentIgnoreCaseContaining(String title, String content);
}
