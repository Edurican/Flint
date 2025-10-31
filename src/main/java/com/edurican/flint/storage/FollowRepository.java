package com.edurican.flint.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    FollowEntity findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<FollowEntity> findByFollowerId(Long followerId);
    Page<FollowEntity> findByFollowerId(Long followerId, Pageable pageable);

    List<FollowEntity> findByFollowingId(Long followingId);
    Page<FollowEntity> findByFollowingId(Long followingId, Pageable pageable);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    int deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
