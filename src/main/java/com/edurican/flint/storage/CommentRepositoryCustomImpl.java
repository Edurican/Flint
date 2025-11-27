package com.edurican.flint.storage;

import com.edurican.flint.core.domain.Comment;
import com.edurican.flint.core.domain.QComment;
import com.edurican.flint.core.enums.EntityStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    private final QComment comment = QComment.comment;
    private final QComment grandchild = new QComment("grandchild");

    @Override
    public List<Comment> findChildren(Long postId, Long parentCommentId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.postId.eq(postId),
                       comment.parentCommentId.eq(parentCommentId),
                        comment.status.eq(EntityStatus.ACTIVE)
                        )
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    @Override
    public long countAllByPost(Long postId) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.postId.eq(postId),
                        comment.status.eq(EntityStatus.ACTIVE)
                )
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public int incrementLikeCount(Long commentId) {
        return (int) queryFactory
                .update(comment)
                .set(comment.likeCount, comment.likeCount.add(1))
                .where(comment.id.eq(commentId))
                .execute();
    }

    @Override
    public int decrementLikeCount(Long commentId) {
        return (int) queryFactory
                .update(comment)
                .set(comment.likeCount, comment.likeCount.subtract(1))
                .where(
                        comment.id.eq(commentId),
                        comment.likeCount.gt(0)
                )
                .execute();
    }

    @Override
    public List<Comment> findRootCommentsWithCursor(Long postId, Long cursor, int limit) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.postId.eq(postId));
        builder.and(comment.depth.eq(0));
        builder.and(comment.status.eq(EntityStatus.ACTIVE));

        if (cursor != null) {
            builder.and(comment.id.lt(cursor));
        }

        return queryFactory
                .selectFrom(comment)
                .where(builder)
                .orderBy(comment.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public long countDepth2ByRoot(Long rootId) {

        Long count = queryFactory
                .select(grandchild.count())
                .from(comment)
                .join(grandchild).on(grandchild.parentCommentId.eq(comment.id))
                .where(
                        comment.parentCommentId.eq(rootId),
                        comment.status.eq(EntityStatus.ACTIVE),
                        grandchild.status.eq(EntityStatus.ACTIVE)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }
}
