package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
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
    public Cursor<FollowResponse> findFollowingsByUserId(Long userId, Long lastFetchedId, Integer limit) {
        return null;
    }

    @Override
    public Cursor<FollowResponse> findFollowersByUserId(Long userId, Long lastFetchedId, Integer limit) {
        return null;
    }

    @Override
    public Cursor<FollowResponse> searchUsers(Long userId, String keyword, Long cursor, Integer limit) {
        return null;
    }
}
