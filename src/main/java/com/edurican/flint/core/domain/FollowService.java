package com.edurican.flint.core.domain;


import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.OffsetLimit;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
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
    public Cursor<Follow> getFollowers(Long userId, Long lastFetchedId, Integer limit) {

        // 현재 쿼리 3번이 지나치게 발생중 JOIN해야하지 않을까 싶음

        // 유저 존재하는지 확인
        // 유저 id가 없을 수 있는지 물어보기
        if (!userRepository.existsById(userId)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 팔로워 조회
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;
        Slice<FollowEntity> followers = followRepository.findByFollowingIdWithCursor(userId, cursor, limit);

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
                        .username(users.get(follow.getFollowerId()) == null ? "" : users.get(follow.getFollowerId()).getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build()
                )
                .toList();

        Long nextCursor = (followers.getContent().isEmpty()) ? null : followers.getContent().get(followers.getContent().size() - 1).getId();
        Boolean hasNext = (followers.getContent().size() == limit) ? true : false;
        return new Cursor<>(follows, nextCursor, hasNext);
    }

    /**
     * 유저의 following 얻기
     */
    @Transactional(readOnly = true)
    public Cursor<Follow> getFollowing(Long userId, Long lastFetchedId, Integer limit) {

        // 유저 존재하는지 확인
        if (!userRepository.existsById(userId)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 팔로잉 조회
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;
        Slice<FollowEntity> following = followRepository.findByFollowerIdWithCursor(userId, cursor, limit);

        // 유저 정보를 얻기 위한 Id 분리
        List<Long> followerIds = following.getContent().stream().map(FollowEntity::getFollowingId).toList();

        // 유저 정보 얻기
        Map<Long, UserEntity> users = userRepository.findAllById(followerIds).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        // 정보 변환
        List<Follow> follows = following.getContent().stream()
                .map(follow -> Follow.builder()
                        .id(follow.getId())
                        .followId(follow.getFollowingId())
                        .username(users.get(follow.getFollowingId()) == null ? "" : users.get(follow.getFollowingId()).getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build()
                )
                .toList();

        Long nextCursor = (following.getContent().isEmpty()) ? null : following.getContent().get(following.getContent().size() - 1).getId();
        Boolean hasNext = (following.getContent().size() == limit) ? true : false;
        return new Cursor<>(follows, nextCursor, hasNext);
    }

    /**
     * 팔로우 추천 피드
     */
    public Cursor<Follow> searchFollow(Long userId, Long lastFetchedId, Integer limit) {

        // 유저 존재하는지 확인
        if (!userRepository.existsById(userId)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;
        Slice<FollowEntity> following = followRepository.findByFollowerIdWithCursor(userId, cursor, limit);

        List<Follow> follows = new ArrayList<>();
        return new Cursor<>(follows, 0L, true);
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

        // 저장
        try {
            followRepository.save(new FollowEntity(userId, followId));
        } catch (Exception e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(Long userId, Long unfollowId) {

        // 삭제
        int deleteCount = followRepository.deleteByFollowerIdAndFollowingId(userId, unfollowId);
        if (deleteCount <= 0) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
