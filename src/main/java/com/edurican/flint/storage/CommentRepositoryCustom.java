package com.edurican.flint.storage;
import com.edurican.flint.core.domain.Comment;
import com.edurican.flint.core.enums.EntityStatus;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {

    List<Comment> findChildren(Long postId, Long parentCommentId);

    long countAllByPost(Long postId);

    int incrementLikeCount(Long commentId);

    int decrementLikeCount(Long commentId);

    List<Comment> findRootCommentsWithCursor(Long postId, Long cursor, int limit);

    long countDepth2ByRoot(Long rootId);
}
