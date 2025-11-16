package com.edurican.flint.core.domain;


import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.request.CursorRequest;
import com.edurican.flint.core.support.utils.CursorUtil;
import com.edurican.flint.storage.*;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Autowired
    public FollowService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    /**
     * 유저의 follower 얻기
     */
    @Transactional(readOnly = true)
    public Cursor<Follow> getFollowers(String username, CursorRequest cursor) {

        // 유저 존재하는지 확인
        if (!userRepository.existsByUsername(username)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 유저 팔로워 조회
        return followRepository.findFollowersByUsername(
                username,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );
    }

    /**
     * 유저의 following 얻기
     */
    @Transactional(readOnly = true)
    public Cursor<Follow> getFollowing(String username, CursorRequest cursor) {

        // 유저 존재하는지 확인
        if (!userRepository.existsByUsername(username)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 유저 팔로잉 조회
        return followRepository.findFollowingByUsername(
                username,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );
    }

    /**
     *  팔로우 검색 우선순위
     *  1. 맞팔로우
     *  2. 2촌
     *  3. 추천 유저
     */
    public Cursor<Follow> searchFollow(UserEntity user, String keyword, CursorRequest cursor) {

        // 유저 존재하는지 확인
        if (!userRepository.existsById(user.getId())) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // keyword가 null이면 그냥 맞팔로우 추천인거고 아니면 keyword포함해서 추천해야하는거임
        // 아니 근데 문제가 그럼 cursor id는 뭐가 맞는거임??
        // 맞팔로우에서 다 소진되었다는 것을 어떻게 다음 요청에서 알 수 있음??
        
        // 1. 맞팔로우
        // 유저를 팔로우하였지만 맞팔로우는 아닌 유저 검색
        Cursor<Follow> notFollowBacks = followRepository.findFollowersNotFollowBack(
                user.getId(),
                keyword,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        // 2. 2촌
        // 유저가 팔로우한 사람이 팔로우한 유저 검색
        Cursor<Follow> secondDegreeConnections = followRepository.findSecondDegreeConnections(
                user.getId(),
                keyword,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        // 3. 추천 유저
        // 인기 유저와 최근 활동한 유저
        Cursor<Follow> recommendedFollowers = followRepository.findRecommendedFollowers(
                user.getId(),
                keyword,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        return new Cursor<>(
                users,
                CursorUtil.nextCursor(users).getId(),
                CursorUtil.hasNextCursor(users, request.getLimit())
        );
    }

    /**
     * 유저 팔로우
     */
    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void follow(Long followerId, Long followingId) {

        // 자기 자신을 팔로우 할 수 없음
        if (followerId.equals(followingId)) {
            throw new CoreException(ErrorType.SELF_FOLLOW_NOT_ALLOWED);
        }

        // 유저 팔로우 또는 맞팔로우
        followRepository.save(new FollowEntity(followerId, followingId));

        // 팔로워 유저 팔로잉 1 증가
        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        follower.incrementFollowingCount();

        // 팔로잉 유저 팔로워 1 증가
        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        following.incrementFollowersCount();
    }

    /**
     * 유저 언팔로우
     */
    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void unfollow(Long followerId, Long followingId) {

        // 자기 자신을 언팔로우 할 수 없음
        if (followerId.equals(followingId)) {
            throw new CoreException(ErrorType.SELF_FOLLOW_NOT_ALLOWED);
        }

        // 유저 언팔로우
        int deleteCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        if (deleteCount <= 0) {
            throw new CoreException(ErrorType.NOT_FOLLOWING);
        }

        // 팔로워 유저 팔로잉 1 감소
        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        follower.decrementFollowingCount();

        // 팔로잉 유저 팔로워 1 감소
        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        following.decrementFollowersCount();
    }
}
