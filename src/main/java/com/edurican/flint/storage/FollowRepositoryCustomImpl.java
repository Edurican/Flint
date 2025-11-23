package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.domain.QFollow;
import com.edurican.flint.core.domain.QUser;
import com.edurican.flint.core.support.Cursor;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     *  내가 팔로워한 유저들
     */
    @Override
    public Cursor<FollowResponse> findFollowingsByUserId(Long userId, Long lastFetchedId, Integer limit) {
        QUser user = QUser.user;
        QFollow follow = QFollow.follow;
        QFollow followCheck = new QFollow("followCheck");

        // 팔로워와 유저의 아이디가 같다면 팔로잉을 얻을 수 있음
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follow.followerId.eq(userId));

        if(lastFetchedId != null) {
            builder.and(user.id.lt(lastFetchedId));
        }

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        follow.id,
                        user.username,
                        user.bio,
                        user.followersCount,
                        JPAExpressions
                                .selectOne()
                                .from(followCheck)
                                .where(followCheck.followerId.eq(userId)
                                        .and(followCheck.followingId.eq(user.id)))
                                .exists()
                ))
                .from(follow)
                .join(user).on(user.id.eq(follow.followingId))
                .where(builder)
                .orderBy(follow.id.desc())
                .limit(limit + 1)
                .fetch();

        return makeCursor(result, limit);
    }

    /**
     *  나를 팔로워한 유저
     */
    @Override
    public Cursor<FollowResponse> findFollowersByUserId(Long userId, Long lastFetchedId, Integer limit) {
        QUser user = QUser.user;
        QFollow follow = QFollow.follow;
        QFollow followCheck = new QFollow("followCheck");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follow.followingId.eq(userId));

        if(lastFetchedId != null) {
            builder.and(user.id.lt(lastFetchedId));
        }

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        follow.id,
                        user.username,
                        user.bio,
                        user.followersCount,
                        JPAExpressions
                                .selectOne()
                                .from(followCheck)
                                .where(followCheck.followerId.eq(userId)
                                        .and(followCheck.followingId.eq(user.id)))
                                .exists()
                ))
                .from(follow)
                .join(user).on(user.id.eq(follow.followerId))
                .where(builder)
                .orderBy(follow.id.desc())
                .limit(limit + 1)
                .fetch();

        return makeCursor(result, limit);
    }

    /**
     *  유저 검색
     */
    @Override
    public Cursor<FollowResponse> searchUsers(Long userId, String username, Long lastFetchedId, Integer limit) {
        QUser user = QUser.user;
        QFollow myFollow = new QFollow("myFollow");
        QFollow otherFollow = new QFollow("otherFollow");

        // builder
        // - username 검색
        // - 마지막 값보다 작은 값 조회
        // - 자기 자신은 제외
        // - 맞팔 제외
        BooleanBuilder builder = new BooleanBuilder();

        if(username != null && !username.isBlank()) {
            builder.and(user.username.like(username + "%"));
        }

        if(lastFetchedId != null) {
            builder.and(user.id.lt(lastFetchedId));
        }

        builder.and(user.id.ne(userId));
        builder.and(
                JPAExpressions
                        .selectOne()
                        .from(myFollow)
                        .where(myFollow.followerId.eq(userId).and(myFollow.followingId.eq(user.id)))
                        .notExists()
                        .or(
                                JPAExpressions
                                        .selectOne()
                                        .from(otherFollow)
                                        .where(otherFollow.followingId.eq(user.id).and(otherFollow.followerId.eq(userId)))
                                        .notExists()
                        )
        );

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        Expressions.constant(0L),
                        user.username,
                        user.bio,
                        user.followersCount,
                        JPAExpressions
                                .selectOne()
                                .from(otherFollow)
                                .where(otherFollow.followerId.eq(user.id)
                                        .and(otherFollow.followingId.eq(userId)))
                                .exists()
                ))
                .from(user)
                .where(builder)
                .orderBy(user.id.desc())
                .limit(limit + 1)
                .fetch();

        return makeCursor(result, limit);
    }

    private Cursor<FollowResponse> makeCursor(List<FollowResponse> result, Integer limit) {
        boolean hasNext = result.size() > limit;
        long nextCursor = 0L;
        if(hasNext) {
            result.remove(limit.intValue());
            nextCursor = result.get(result.size() - 1).followId();
        }
        return new Cursor<>(result, nextCursor, hasNext);
    }
}
