package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    CommentEntity findByIdAndUserId(Long userId, Long commentId);
}
