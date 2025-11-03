package com.edurican.flint.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface    CommentRepository extends JpaRepository<CommentEntity, Long> {
    CommentEntity findById(long id);

    @Query("select c.parentCommentId from CommentEntity c where c.id = :id")
    Long findParentIdById(@Param("id") Long id);

    CommentEntity findByIdAndUserId(Long userId, Long commentId);

    /** 특정 게시글의 최상위 댓글 (parentCommentId IS NULL) */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId is null and c.status = 'ACTIVE' order by c.createdAt desc")
    List<CommentEntity> findTopLevelByPost(@Param("postId") Long postId);

    /** 특정 댓글의 자식 댓글 (대댓글, 대대댓글 포함) */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId = :parentId and c.status = 'ACTIVE' order by c.createdAt asc")
    List<CommentEntity> findChildren(@Param("postId") Long postId, @Param("parentId") Long parentId);

    /** 특정 댓글의 자식 댓글 수 (대댓글 개수) */
    @Query("select count(c) from CommentEntity c where c.parentCommentId = :parentId and c.status = 'ACTIVE'")
    Long countChildren(@Param("parentId") Long parentId);

    /** 부모 댓글이 해당 게시글에 속하는지 검증 (부모-게시글 일치 여부) */
    @Query("select (count(c) > 0) from CommentEntity c where c.id = :id and c.postId = :postId and c.status = 'ACTIVE'")
    boolean existsActiveOnPost(@Param("id") Long id, @Param("postId") Long postId);

    /** 댓글 활성 상태로 단건 조회 */
    @Query("select c from CommentEntity c where c.id = :id and c.status = 'ACTIVE'")
    Optional<CommentEntity> findActiveById(@Param("id") Long id);

    /** 인기순 정렬 (좋아요 많은 순 + 최신순) */
    @Query("select c from CommentEntity c where c.postId = :postId and c.parentCommentId is null and c.status = 'ACTIVE' order by c.likeCount desc, c.createdAt desc")
    List<CommentEntity> findTopLevelByPostOrderByPopular(@Param("postId") Long postId);

    /** 커서 기반 최상위 댓글: id DESC로 내려가며 (:cursor가 있으면 그 이전만) */
    @Query("""
           select c
             from CommentEntity c
            where c.postId = :postId
              and c.parentCommentId is null
              and c.status = 'ACTIVE'
              and (:cursor is null or c.id < :cursor)
            order by c.id desc
           """)
    Slice<CommentEntity> findTopLevelByPostWithCursor(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    /** 커서 기반 자식 댓글: 특정 parent의 자식만, id DESC로 내려가며 커서 이전만 */
    @Query("""
           select c
             from CommentEntity c
            where c.postId = :postId
              and c.parentCommentId = :parentId
              and c.status = 'ACTIVE'
              and (:cursor is null or c.id < :cursor)
            order by c.id desc
           """)
    Slice<CommentEntity> findChildrenWithCursor(
            @Param("postId") Long postId,
            @Param("parentId") Long parentId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

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

    /** 직계 자식 수(대댓글 수) — 카드 요약 숫자 표시 용도 */
    @Query(value =
            "SELECT COUNT(1) " +
                    "FROM comments c " +
                    "WHERE c.parent_id = :parentId " +
                    "  AND c.status = 'ACTIVE'",
            nativeQuery = true)
    Long countChildrenNative(@Param("parentId") Long parentId);
}
