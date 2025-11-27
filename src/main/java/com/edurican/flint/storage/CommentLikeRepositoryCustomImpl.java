package com.edurican.flint.storage;

import com.edurican.flint.core.domain.QCommentLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryCustomImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCommentLike commentLike = QCommentLike.commentLike;

    @Override
    public Optional<Long> countByCommentIdOptional(Long commentId) {
        Long count = queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .where(commentLike.commentId.eq(commentId))
                .fetchOne();
        return Optional.ofNullable(count);
    }
}
