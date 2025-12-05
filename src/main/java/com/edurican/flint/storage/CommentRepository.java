package com.edurican.flint.storage;


import com.edurican.flint.core.domain.Comment;
import com.edurican.flint.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndStatus(Long id, EntityStatus status);

    boolean existsByParentCommentIdAndStatus(Long parentCommentId, EntityStatus status);

    long countByParentCommentIdAndStatus(Long parentCommentId, EntityStatus status);
}
