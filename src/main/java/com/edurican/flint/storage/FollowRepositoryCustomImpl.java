package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.QFollow;
import com.edurican.flint.core.domain.QUser;
import com.edurican.flint.core.domain.User;
import com.edurican.flint.core.support.Cursor;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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

        // 마지막으로 검색된 아이디가 있다면
        if (lastFetchedId != null) {
            builder.and(follow.followingId.lt(lastFetchedId));
        } else {
            builder.and(follow.followingId.lt(Long.MAX_VALUE));
        }

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        follow.id,
                        user.username,
                        user.name,
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

        boolean hasNext = result.size() > limit;
        long nextCursor = 0L;
        if(hasNext) {
            result.remove(limit - 1);
            nextCursor = result.get(result.size() - 1).followId();
        }

        return new Cursor<>(result, nextCursor, hasNext);
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

        if (lastFetchedId != null) {
            builder.and(follow.followerId.lt(lastFetchedId));
        } else {
            builder.and(follow.followerId.lt(Long.MAX_VALUE));
        }

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        follow.id,
                        user.username,
                        user.name,
                        user.bio,
                        user.followersCount,
                        JPAExpressions
                                .selectOne()
                                .from(followCheck)
                                .where(followCheck.followingId.eq(userId)
                                        .and(followCheck.followerId.eq(user.id)))
                                .exists()
                ))
                .from(follow)
                .join(user).on(user.id.eq(follow.followerId))
                .where(builder)
                .orderBy(follow.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = result.size() > limit;
        long nextCursor = 0L;
        if(hasNext) {
            result.remove(limit - 1);
            nextCursor = result.get(result.size() - 1).followId();
        }

        return new Cursor<>(result, nextCursor, hasNext);
    }

    @Override
    public Cursor<FollowResponse> searchUsers(Long userId, String keyword, Long lastFetchedId, Integer limit) {
        QUser user = QUser.user;
        QFollow follow = QFollow.follow;
        QFollow followCheck = new QFollow("followCheck");

        BooleanBuilder searchBuilder = new BooleanBuilder();
        searchBuilder.and(user.username.like(keyword + "%").or(user.name.like(keyword + "%")));

        BooleanBuilder mutal

        if (lastFetchedId != null) {
            searchBuilder.and(user.id.lt(lastFetchedId));
        } else {
            searchBuilder.and(user.id.lt(Long.MAX_VALUE));
        }

        List<FollowResponse> result = queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        user.id,
                        follow.id,
                        user.username,
                        user.name,
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
                .where(searchBuilder)
                .orderBy(follow.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = result.size() > limit;
        long nextCursor = 0L;
        if(hasNext) {
            result.remove(limit - 1);
            nextCursor = result.get(result.size() - 1).followId();
        }

        return new Cursor<>(result, nextCursor, hasNext);
    }
}
