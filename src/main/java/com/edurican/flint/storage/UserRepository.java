package com.edurican.flint.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FollowEntity f ON f.followerId = :userId AND f.followingId = u.id " +
            "WHERE (:target IS NULL OR u.username LIKE CONCAT('%', :target, '%')) " +
            "  AND f.id IS NULL " +
            "  AND u.id <> :userId " +
            "  AND u.id < :cursor " +
            "ORDER BY u.id DESC")
    Slice<UserEntity> searchByUsernameWithCursor(
            @Param("userId") Long userId,
            @Param("target") String target,
            @Param("cursor") Long cursor,
            Pageable pageable);
}
