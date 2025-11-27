package com.edurican.flint.storage;


import com.edurican.flint.core.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, CommentLikeRepositoryCustom {

    int deleteByUserIdAndCommentId(Long userId, Long commentId);
}
