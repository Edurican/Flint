package com.edurican.flint.storage;


import com.edurican.flint.core.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    int deleteByUserIdAndCommentId(Long userId, Long commentId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    @Query("select count(cl) from CommentLike cl where cl.commentId = :commentId")
    Integer countByCommentId(@Param("commentId") Long commentId);

}
