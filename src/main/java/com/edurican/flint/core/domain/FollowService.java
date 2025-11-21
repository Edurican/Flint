package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.request.CursorRequest;
import com.edurican.flint.core.support.response.CursorResponse;
import com.edurican.flint.storage.*;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CursorResponse<FollowResponse> getFollowers(String username, CursorRequest cursor) {

        // 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 유저 팔로워 조회
        Cursor<FollowResponse> followers = followRepository.findFollowersByUserId(
                user.getId(),
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        return CursorResponse.of(followers);
    }

    /**
     * 유저의 following 얻기
     */
    @Transactional(readOnly = true)
    public CursorResponse<FollowResponse> getFollowing(String username, CursorRequest cursor) {

        // 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 유저 팔로잉 조회
        Cursor<FollowResponse> following = followRepository.findFollowingsByUserId(
                user.getId(),
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        return CursorResponse.of(following);
    }

    /**
     *  팔로우 검색 (최신순)
     */
    public CursorResponse<FollowResponse> searchFollow(User user, String keyword, CursorRequest cursor) {

        // 유저 조회
        if(!userRepository.existsById(user.getId())) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 유저 검색
        Cursor<FollowResponse> searchUsers = followRepository.searchUsers(
                user.getId(),
                keyword,
                cursor.getLastFetchedId(),
                cursor.getLimit()
        );

        return CursorResponse.of(searchUsers);
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
        followRepository.save(new Follow(followerId, followingId));

        // 팔로워 유저 팔로잉 1 증가
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        follower.incrementFollowingCount();

        // 팔로잉 유저 팔로워 1 증가
        User following = userRepository.findById(followingId)
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
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        follower.decrementFollowingCount();

        // 팔로잉 유저 팔로워 1 감소
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        following.decrementFollowersCount();
    }
}
