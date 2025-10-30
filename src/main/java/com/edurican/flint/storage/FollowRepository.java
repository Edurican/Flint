package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    FollowEntity findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<FollowEntity> findByFollowerId(Long followerId);
    List<FollowEntity> findByFollowerId(Long followerId, Pageable pageable);

    List<FollowEntity> findByFollowingId(Long followingId);
    List<FollowEntity> findByFollowingId(Long followingId, Pageable pageable);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    int deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
