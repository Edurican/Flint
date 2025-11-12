package com.edurican.flint.core.domain;


import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.OffsetLimit;
import com.edurican.flint.core.support.Page;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.utils.CursorUtil;
import com.edurican.flint.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Cursor<Follow> getFollowers(String username, Long lastFetchedId, Integer limit) {
        
        // 유저 조회
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND)
        );

        // 팔로워 조회
        Slice<FollowEntity> followers = followRepository.findByFollowingIdWithCursor(
                user.getId(),
                CursorUtil.getCursor(lastFetchedId),
                PageRequest.of(0, limit)
        );

        // 유저 정보를 얻기 위한 Id 분리
        List<Long> followerIds = followers.getContent().stream()
                .map(FollowEntity::getFollowerId)
                .toList();

        // 유저 정보 얻기
        Map<Long, UserEntity> users = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        // 정보 변환
        List<Follow> follows = followers.getContent().stream()
                .map(follow -> Follow.builder()
                        .id(follow.getId())
                        .followId(follow.getFollowerId())
                        .username(users.get(follow.getFollowerId()).getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build()
                )
                .toList();

        return new Cursor<>(
                follows,
                CursorUtil.nextCursor(followers.getContent()).getId(),
                CursorUtil.hasNextCursor(followers.getContent(), limit)
        );
    }

    /**
     * 유저의 following 얻기
     */
    @Transactional(readOnly = true)
    public Cursor<Follow> getFollowing(String username, Long lastFetchedId, Integer limit) {

        // 유저 존재하는지 확인
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND)
        );

        // 팔로잉 조회
        Slice<FollowEntity> following = followRepository.findByFollowerIdWithCursor(
                user.getId(),
                CursorUtil.getCursor(lastFetchedId),
                PageRequest.of(0, limit)
        );

        // 유저 정보를 얻기 위한 Id 분리
        List<Long> followerIds = following.getContent().stream()
                .map(FollowEntity::getFollowingId)
                .toList();

        // 유저 정보 얻기
        Map<Long, UserEntity> users = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        // 정보 변환
        List<Follow> follows = following.getContent().stream()
                .map(follow -> Follow.builder()
                        .id(follow.getId())
                        .followId(follow.getFollowingId())
                        .username(users.get(follow.getFollowingId()).getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build()
                )
                .toList();

        return new Cursor<>(
                follows,
                CursorUtil.nextCursor(following.getContent()).getId(),
                CursorUtil.hasNextCursor(following.getContent(), limit)
        );
    }

    /**
     * 팔로우 추천 피드
     */
    public Cursor<User> searchFollow(UserEntity user, String searchUser, Long lastFetchedId, Integer limit) {

        // 유저 존재하는지 확인
        if (!userRepository.existsById(user.getId())) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 팔로우 최신 버전
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;
        Pageable pageable = PageRequest.of(0, limit);
        Slice<UserEntity> userEntities = userRepository.searchByUsernameWithCursor(user.getId(), searchUser, cursor, pageable);

        // 정보 변환
        List<User> users = userEntities.getContent().stream()
                .map(entity -> User.builder()
                        .id(entity.getId())
                        .username(entity.getUsername())
                        .bio(entity.getBio())
                        .build()
                )
                .toList();

        Long nextCursor = (userEntities.getContent().isEmpty()) ? null : userEntities.getContent().get(userEntities.getContent().size() - 1).getId();
        Boolean hasNext = (userEntities.getContent().size() == limit) ? true : false;
        return new Cursor<>(users, nextCursor, hasNext);
    }

    /**
     * 팔로우
     */
    @Transactional
    public void follow(Long userId, Long followId) {

        // 유저 이름 같은지 확인
        if (userId.equals(followId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        // UserEntity에서 조회 후 팔로워는 user, 팔로잉 하는 대상은 followId로 대입
        UserEntity follower = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        UserEntity following = userRepository.findById(followId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 저장
        try {
            followRepository.save(new FollowEntity(userId, followId));
            // 본인은 팔로잉 +1
            follower.incrementFollowingCount();

            // 팔로우 대상은 팔로워 +1
            following.incrementFollowersCount();
        } catch (Exception e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(Long userId, Long unfollowId) {

        UserEntity follower = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        UserEntity following = userRepository.findById(unfollowId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 삭제
        int deleteCount = followRepository.deleteByFollowerIdAndFollowingId(userId, unfollowId);
        if (deleteCount <= 0) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        // 본인은 팔로잉 -1
        follower.decrementFollowingCount();

        // 언팔로우 대상은 팔로워 -1
        following.decrementFollowersCount();
    }
}
