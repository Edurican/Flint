package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.api.controller.v1.response.PostResponse;
import com.edurican.flint.core.domain.QPost;
import com.edurican.flint.core.domain.QTopic;
import com.edurican.flint.core.domain.QUser;
import com.edurican.flint.core.support.Cursor;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostResponse> findPreferredPosts(List<Long> preferredTopicIds, Long userId, Long lastFetchedId, int limit) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QTopic topic = QTopic.topic;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.userId.ne(userId));                // 자신이 작성한글은 안보임
        builder.and(post.topicId.in(preferredTopicIds));    // 선호하는 토픽이어야함

        if (lastFetchedId != null) {
            builder.and(post.id.lt(lastFetchedId));         // 이전 게시글보다 낮은 글만 조회
        }

        return queryFactory
                .select(Projections.constructor(PostResponse.class,
                        post.id,
                        user.id,
                        post.content,
                        user.username,
                        topic.topicName,
                        post.viewCount,
                        post.commentCount,
                        post.likeCount,
                        post.createdAt
                ))
                .from(post)
                .join(user).on(user.id.eq(post.userId))
                .join(topic).on(topic.id.eq(post.topicId))
                .where(builder)
                .orderBy(post.id.desc())
                .limit(limit + 1)
                .fetch();
    }

    @Override
    public List<PostResponse> findRecommendPosts(List<Long> excludingPostIds, Long userId, Long lastFetchedId, int limit) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QTopic topic = QTopic.topic;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.userId.ne(userId));                    // 자신이 작성한글은 안보임
        builder.and(post.id.notIn(excludingPostIds));           // 중복된 게시글은 포함하지 않음

        if (lastFetchedId != null) {
            builder.and(post.id.lt(lastFetchedId));             // 이전 게시글보다 낮은 글만 조회
        }

        return queryFactory
                .select(Projections.constructor(PostResponse.class,
                        post.id,
                        user.id,
                        post.content,
                        user.username,
                        topic.topicName,
                        post.viewCount,
                        post.commentCount,
                        post.likeCount,
                        post.createdAt
                ))
                .from(post)
                .join(user).on(user.id.eq(post.userId))
                .join(topic).on(topic.id.eq(post.topicId))
                .where(builder)
                .orderBy(post.likeCount.desc(), post.id.desc())
                .limit(limit + 1)
                .fetch();
    }

    @Override
    public List<PostResponse> findByTopicIdWithCursor(Long topicId, Long userId, Long lastFetchedId, int limit) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QTopic topic = QTopic.topic;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.userId.ne(userId));           // 자신이 작성한글은 안보임
        builder.and(post.topicId.eq(topicId));         // 토픽 아이디가 같은 게시물만 검색

        if (lastFetchedId != null) {
            builder.and(post.id.lt(lastFetchedId));     // 이전 게시글보다 낮은 글만 조회
        }

        return queryFactory
                .select(Projections.constructor(PostResponse.class,
                        post.id,
                        user.id,
                        post.content,
                        user.username,
                        topic.topicName,
                        post.viewCount,
                        post.commentCount,
                        post.likeCount,
                        post.createdAt
                ))
                .from(post)
                .join(user).on(user.id.eq(post.userId))
                .join(topic).on(topic.id.eq(post.topicId))
                .where(builder)
                .orderBy(post.id.desc())
                .limit(limit + 1)
                .fetch();
    }
}
