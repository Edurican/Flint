package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {
    List<FollowEntity> findByIdFollowerId(Long followerId);
    List<FollowEntity> findByIdFollowingId(Long followingId);
    boolean existsByIdFollowerIdAndIdFollowingId(Long followerId, Long followingId);
}
