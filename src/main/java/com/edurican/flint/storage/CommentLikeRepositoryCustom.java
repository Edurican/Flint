package com.edurican.flint.storage;

import java.util.Optional;

public interface CommentLikeRepositoryCustom {
    Optional<Long> countByCommentIdOptional(Long commentId);
}
