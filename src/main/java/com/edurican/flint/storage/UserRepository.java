package com.edurican.flint.storage;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT p FROM PostEntity p WHERE p.id < :cursor ORDER BY p.id DESC LIMIT :limit")
    Slice<PostEntity> findAllWithCursor(@Param("cursor") Long cursor, Integer limit);

    @Query("SELECT p FROM PostEntity p WHERE p.userId = :userId AND p.id < :cursor ORDER BY p.id DESC LIMIT :limit")
    Slice<PostEntity> findByUserIdWithCursor(@Param("userId") Long userId, @Param("cursor") Long cursor, Integer limit);


}
