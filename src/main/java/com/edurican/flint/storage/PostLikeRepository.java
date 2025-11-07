package com.edurican.flint.storage;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    int deleteByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT p FROM PostEntity p WHERE p.topicId = :topicId AND p.id < :cursor ORDER BY p.id DESC LIMIT :limit")
    Slice<PostEntity> findByTopicIdWithCursor(@Param("topicId") Long topicId, @Param("cursor") Long cursor, Integer limit);


}
