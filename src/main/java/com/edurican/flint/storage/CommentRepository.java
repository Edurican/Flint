package com.edurican.flint.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("select c.parentCommentId from CommentEntity c where c.id = :id")
    Long findParentIdById(@Param("id") Long id);

    CommentEntity findByIdAndUserId(Long userId, Long commentId);

    /** 특정 게시글의 최상위 댓글 */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId is null and c.status = 'ACTIVE' order by c.createdAt desc")
    List<CommentEntity> findTopLevelByPost(@Param("postId") Long postId);

    /** 특정 댓글의 자식 댓글 (대댓글, 대대댓글 포함) */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId = :parentId and c.status = 'ACTIVE' order by c.createdAt asc")
    List<CommentEntity> findChildren(@Param("postId") Long postId, @Param("parentId") Long parentId);

    /** 특정 댓글의 자식 댓글 수 (대댓글 개수) */
    @Query("select count(c) from CommentEntity c where c.parentCommentId = :parentId and c.status = 'ACTIVE'")
    Long countChildren(@Param("parentId") Long parentId);

    /** 게시글 전체 댓글을 커서(ID) 기준으로 내림차순 페이징 */
    @Query(value =
            "SELECT * " +
                    "FROM comments c " +
                    "WHERE c.post_id = :postId " +
                    "  AND c.status = 'ACTIVE' " +
                    "  AND (:cursor IS NULL OR c.id < :cursor) " +
                    "ORDER BY c.id DESC " +
                    "LIMIT :limit",
            nativeQuery = true)
    List<CommentEntity> findByPostWithCursorNative(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );

    /** 직계 자식 수(대댓글 수) */
    @Query(value =
            "SELECT COUNT(1) " +
                    "FROM comments c " +
                    "WHERE c.parent_id = :parentId " +
                    "  AND c.status = 'ACTIVE'",
            nativeQuery = true)
    Long countChildrenNative(@Param("parentId") Long parentId);

    @Query(value = """
        SELECT COUNT(1)
          FROM comments c
         WHERE c.post_id = :postId
           AND c.status = 'ACTIVE'
        """, nativeQuery = true)
    long countAllByPost(@Param("postId") Long postId);

    // 루트 댓글(뎁스1)만 커서로
    @Query(value = """
        SELECT *
          FROM comments c
         WHERE c.post_id = :postId
           AND c.parent_id IS NULL
           AND c.status = 'ACTIVE'
           AND (:cursor IS NULL OR c.id < :cursor)
         ORDER BY c.id DESC
         LIMIT :limit
        """, nativeQuery = true)
    List<CommentEntity> findTopLevelByPostWithCursorNative(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );

    // 특정 parent의 자식(뎁스2/3)만 커서로
    @Query(value = """
        SELECT *
          FROM comments c
         WHERE c.post_id = :postId
           AND c.parent_id = :parentId
           AND c.status = 'ACTIVE'
           AND (:cursor IS NULL OR c.id < :cursor)
         ORDER BY c.id ASC     -- 대댓글은 보통 오래된 순
         LIMIT :limit
        """, nativeQuery = true)
    List<CommentEntity> findChildrenWithCursorNative(
            @Param("postId") Long postId,
            @Param("parentId") Long parentId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );
}
