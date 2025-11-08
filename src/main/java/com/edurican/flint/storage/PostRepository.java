package com.edurican.flint.storage;

import com.edurican.flint.core.enums.EntityStatus;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity,Long> {
    
    List<PostEntity> findByUserIdOrderByIdDesc(Long userId);
    List<PostEntity> findByTopicId(Long topicId);

    @Query("SELECT p FROM PostEntity p WHERE p.id < :cursor ORDER BY p.id DESC LIMIT :limit")
    Slice<PostEntity> findByWithCursor(@Param("cursor") Long cursor, @Param("limit") Integer limit);

    @Query("SELECT p FROM PostEntity p WHERE p.topicId = :topicId AND p.id < :cursor ORDER BY p.id DESC LIMIT :limit")
    Slice<PostEntity> findByTopicIdWithCursor(@Param("topicId") Long topicId, @Param("cursor") Long cursor, @Param("limit") Integer limit);

    // 좋아요 증감
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    int incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount>0")
    int decrementLikeCount(@Param("postId") Long postId);

    // 활성된 게시물 검색
    @Query("SELECT count(p) FROM PostEntity p WHERE p.userId = :userId and p.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") EntityStatus status);

}
