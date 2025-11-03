package com.edurican.flint.storage;

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

    // 특정 게시글의 최상위 댓글
 //    List<CommentEntity> findAllByPostIdAndParentCommentIdIsNull(Long postId);

    // 특정 댓글의 자식 댓글 (대댓글/대대댓글)
//     List<CommentEntity> findAllByPostIdAndParentCommentId(Long postId, Long parentId);
}
