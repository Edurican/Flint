package com.edurican.flint.storage;

import com.edurican.flint.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndStatus(Long id, EntityStatus status);

    /** 특정 댓글의 자식 댓글 (대댓글, 대대댓글 포함) */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId = :parentCommentId and c.status = 'ACTIVE' order by c.createdAt asc")
    List<CommentEntity> findChildren(@Param("postId") Long postId, @Param("parentCommentId") Long parentCommentId);

    @Query(value = """
        SELECT COUNT(1)
          FROM comments c
         WHERE c.post_id = :postId
           AND c.status = 'ACTIVE'
        """, nativeQuery = true)
    long countAllByPost(@Param("postId") Long postId);

    // 자식 개수 (더보기 버튼용)
    long countByParentCommentIdAndStatus(Long parentCommentId, EntityStatus status);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
    int incrementLikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount - 1 WHERE c.id  = :commentId AND c.likeCount>0")
    int decrementLikeCount(@Param("commentId") Long commentId);

    @Query(value = """
    SELECT *
    FROM comments c
    WHERE c.post_id = :postId
      AND c.depth = 0
      AND c.status = 'ACTIVE'
      AND (:cursor IS NULL OR c.id < :cursor)
    ORDER BY c.id DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<CommentEntity> findRootCommentsWithCursor(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    // 루트 댓글(rootId)의 depth2(손자) 개수
    @Query("""
    SELECT COUNT(gc)
    FROM CommentEntity c
    JOIN CommentEntity gc ON gc.parentCommentId = c.id
    WHERE c.parentCommentId = :rootId
      AND c.status = com.edurican.flint.core.enums.EntityStatus.ACTIVE
      AND gc.status = com.edurican.flint.core.enums.EntityStatus.ACTIVE
""")
    long countDepth2ByRoot(@Param("rootId") Long rootId);

}
