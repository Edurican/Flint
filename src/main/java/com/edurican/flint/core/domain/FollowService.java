package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.request.CursorRequest;
import com.edurican.flint.core.support.response.CursorResponse;
import com.edurican.flint.storage.FollowRepository;
import com.edurican.flint.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

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
                cursor.lastFetchedId(),
                cursor.limit()
        );

        return new CursorResponse<>(
                followers.getContents(),
                followers.getLastFetchedId(),
                followers.getHasNext()
        );
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
                cursor.lastFetchedId(),
                cursor.limit()
        );

        return new CursorResponse<>(
                following.getContents(),
                following.getLastFetchedId(),
                following.getHasNext()
        );
    }

    /**
     *  팔로우 검색 (최신순)
     */
    public CursorResponse<FollowResponse> searchFollow(User user, String username, CursorRequest cursor) {

        // 유저 조회
        if(!userRepository.existsById(user.getId())) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 유저 검색
        Cursor<FollowResponse> searchUsers = followRepository.searchUsers(
                user.getId(),
                username,
                cursor.lastFetchedId(),
                cursor.limit()
        );

        return new CursorResponse<>(
                searchUsers.getContents(),
                searchUsers.getLastFetchedId(),
                searchUsers.getHasNext()
        );
    }

    /**
     *  유저 팔로우
     */
    @Transactional
    public void follow(Long followerId, Long followingId) {
        // 자기 자신을 팔로우 할 수 없음
        if (followerId.equals(followingId)) {
            throw new CoreException(ErrorType.CANNOT_FOLLOW_SELF);
        }

        // User Id가 낮은 순으로 락 잠금
        Long firstId = Math.min(followerId, followingId);
        Long secondId = Math.max(followerId, followingId);

        User firstUser = userRepository.findByIdWithLock(firstId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        User secondUser = userRepository.findByIdWithLock(secondId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));


        // 이미 팔로우 했는지 확인
        if(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new CoreException(ErrorType.ALREADY_FOLLOWING);
        }

        // 유저 팔로우 또는 맞팔로우
        followRepository.save(new Follow(followerId, followingId));

        // 팔로워 유저 팔로잉 1 증가
        if (followingId.equals(firstUser.getId())) {
            firstUser.incrementFollowingCount();
        } else {
            secondUser.incrementFollowingCount();
        }

        // 팔로잉 유저 팔로워 1 증가
        if (followerId.equals(firstUser.getId())) {
            firstUser.incrementFollowersCount();
        } else {
            secondUser.incrementFollowersCount();
        }
    }

    /**
     * 유저 언팔로우
     */
    @Transactional
    public void unfollow(Long followerId, Long followingId) {

        // 자기 자신을 언팔로우 할 수 없음
        if (followerId.equals(followingId)) {
            throw new CoreException(ErrorType.CANNOT_FOLLOW_SELF);
        }

        // User Id가 낮은 순으로 락 잠금
        Long firstId = Math.min(followerId, followingId);
        Long secondId = Math.max(followerId, followingId);

        User firstUser = userRepository.findByIdWithLock(firstId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        User secondUser = userRepository.findByIdWithLock(secondId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));


        // 유저 언팔로우
        int deleteCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        if (deleteCount <= 0) {
            throw new CoreException(ErrorType.NOT_FOLLOWING);
        }

        // 팔로워 유저 팔로잉 1 감소
        if (followingId.equals(firstUser.getId())) {
            firstUser.decrementFollowingCount();
        } else {
            secondUser.decrementFollowingCount();
        }

        // 팔로잉 유저 팔로워 1 감소
        if (followerId.equals(firstUser.getId())) {
            firstUser.decrementFollowersCount();
        } else {
            secondUser.decrementFollowersCount();
        }
    }
}
