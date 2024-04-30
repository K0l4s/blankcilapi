package com.blankcil.api.blankcilapi.repository;

import com.blankcil.api.blankcilapi.entity.PodcastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PodcastRepository extends JpaRepository<PodcastEntity, Integer> {
}
