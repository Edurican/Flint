package com.edurican.flint.storage;

import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.User;
import com.edurican.flint.core.support.Cursor;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Cursor<Follow> findFollowingByUsername(String username, Long lastFetchedId, Integer limit) {
        QFollowEntity followEntity = QFollowEntity.followEntity;
        QUserEntity follower = new QUserEntity("follower");
        QUserEntity following = new QUserEntity("following");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follower.username.eq(username));
        if (lastFetchedId != null) {
            builder.and(followEntity.id.lt(lastFetchedId));
        }

        List<Follow> contents = queryFactory
                .select(Projections.constructor(Follow.class,
                                followEntity.id,
                                following.id,
                                following.username,
                                following.name,
                                following.bio,
                                following.followersCount,
                                following.followingCount,
                                following.createdAt
                        )
                )
                .from(followEntity)
                .join(follower).on(followEntity.followerId.eq(follower.id))
                .join(following).on(followEntity.followingId.eq(following.id))
                .where(builder)
                .orderBy(followEntity.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = contents.size() > limit;
        if (hasNext) {
            contents = contents.subList(0, limit);
        }
        Long lastId = contents.isEmpty() ? null : contents.get(contents.size() - 1).followId();

        return new Cursor<>(contents, lastId, hasNext, null);
    }

    @Override
    public Cursor<Follow> findFollowersByUsername(String username, Long lastFetchedId, Integer limit) {
        QFollowEntity followEntity = QFollowEntity.followEntity;
        QUserEntity follower = new QUserEntity("follower");
        QUserEntity following = new QUserEntity("following");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(following.username.eq(username));
        if (lastFetchedId != null) {
            builder.and(followEntity.id.lt(lastFetchedId));
        }

        List<Follow> contents = queryFactory
                .select(Projections.constructor(Follow.class,
                                followEntity.id,
                                follower.id,
                                follower.username,
                                follower.name,
                                follower.bio,
                                follower.followersCount
                        )
                )
                .from(followEntity)
                .join(follower).on(followEntity.followerId.eq(follower.id))
                .join(following).on(followEntity.followingId.eq(following.id))
                .where(builder)
                .orderBy(followEntity.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = contents.size() > limit;
        if (hasNext) {
            contents = contents.subList(0, limit);
        }
        Long lastId = contents.isEmpty() ? null : contents.get(contents.size() - 1).followId();

        return new Cursor<>(contents, lastId, hasNext, null);
    }

    @Override
    public Cursor<Follow> findFollowersNotFollowBack(Long userId, String keyword, Long lastFetchedId, Integer limit) {
        QFollowEntity followEntity = QFollowEntity.followEntity;
        QUserEntity follower = new QUserEntity("follower");
        QUserEntity following = new QUserEntity("following");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(following.id.eq(userId));

        if (lastFetchedId != null) {
            builder.and(followEntity.id.lt(lastFetchedId));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(follower.username.containsIgnoreCase(keyword).or(follower.name.containsIgnoreCase(keyword)));
        }

        List<Follow> contents = queryFactory
                .selectDistinct(Projections.constructor(Follow.class,
                                followEntity.id,
                                follower.id,
                                follower.username,
                                follower.name,
                                follower.bio,
                                follower.followersCount
                        )
                )
                .from(followEntity)
                .join(follower).on(followEntity.followerId.eq(follower.id))
                .join(following).on(followEntity.followingId.eq(following.id))
                .where(builder)
                .orderBy(followEntity.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = contents.size() > limit;
        if (hasNext) {
            contents = contents.subList(0, limit);
        }
        Long lastId = contents.isEmpty() ? null : contents.get(contents.size() - 1).followId();

        return new Cursor<>(contents, lastId, hasNext, "MUTUAL_FOLLOW");
    }

    @Override
    public Cursor<Follow> findSecondDegreeConnections(Long userId, String keyword, Long lastFetchedId, Integer limit) {
        return null;
    }

    @Override
    public Cursor<Follow> findRecommendedFollowers(Long userId, String keyword, Long lastFetchedId, Integer limit) {
        return null;
    }
}
