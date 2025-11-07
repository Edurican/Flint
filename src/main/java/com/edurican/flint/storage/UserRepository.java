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
            "WHERE (:target IS NULL OR u.username LIKE CONCAT('%', :target, '%')) " +
            "AND u.id < :cursor " +
            "ORDER BY u.id DESC")
    Slice<UserEntity> searchByUsernameWithCursor(
            @Param("target") String target,
            @Param("cursor") Long cursor,
            Pageable pageable);
}
