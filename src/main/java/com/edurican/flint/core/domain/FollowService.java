package com.edurican.flint.core.domain;


import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<Follow> getFollowers(Long userId) {

        // 유저 존재하는지 확인
        if(!userRepository.existsById(userId)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 팔로워 가져오기
        Map<Long, FollowEntity> followers = 
                followRepository.findByFollowingId(userId).stream()
                        .collect(Collectors.toMap(FollowEntity::getFollowingId, Function.identity()));

        // 팔로워 정보 얻기
        return userRepository.findAllById(followers.keySet()).stream()
                .map(user ->
                        Follow.builder()
                                .followId(user.getId())
                                .username(user.getUsername())
                                .createdAt(followers.get(user.getId()).getCreatedAt())
                                .build()
                )
                .toList();
    }

    /**
     * 유저의 following 얻기
     */
    @Transactional(readOnly = true)
    public List<Follow> getFollowing(Long userId) {

        // 유저 존재하는지 확인
        if(!userRepository.existsById(userId)) {
            throw new CoreException(ErrorType.USER_NOT_FOUND);
        }

        // 팔로잉 가져오기
        Map<Long, FollowEntity> followings =
                followRepository.findByFollowerId(userId).stream()
                        .collect(Collectors.toMap(FollowEntity::getFollowingId, Function.identity()));

        //  팔로잉 정보 얻기
        return userRepository.findAllById(followings.keySet()).stream()
                .map(user ->
                        Follow.builder()
                                .followId(user.getId())
                                .username(user.getUsername())
                                .build()
                )
                .toList();
    }

    /**
     * 팔로우
     */
    @Transactional
    public void follow(Long userId, Long followId) {

        // 유저 이름 같은지 확인
        if(userId.equals(followId)) {
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
        if(deleteCount <= 0) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
