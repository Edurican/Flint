package com.edurican.flint.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    FollowEntity findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT f " +
            " FROM FollowEntity f " +
            "FULL OUTER JOIN UserEntity u ON f.followerId = u.id" +
            "WHERE f.followerId = :userId AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    Slice<FollowEntity> searchByFollowIdWithCursor(@Param("userId") Long userId, @Param("cursor") Long cursor, @Param("limit") Integer limit);

    @Query("SELECT f FROM FollowEntity f WHERE f.followerId = :followerId AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    Slice<FollowEntity> findByFollowerIdWithCursor(@Param("followerId") Long followerId, @Param("cursor") Long cursor, @Param("limit") Integer limit);

    @Query("SELECT f FROM FollowEntity f WHERE f.followingId = :followingId AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    Slice<FollowEntity> findByFollowingIdWithCursor(@Param("followingId") Long followingId, @Param("cursor") Long cursor, @Param("limit") Integer limit);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    int deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
