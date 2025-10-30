package com.edurican.flint.core.domain;


import com.edurican.flint.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional(readOnly = true)
    public List<Follow> getFollowers(Long userId) {

        boolean isExists = userRepository.existsById(userId);
        if (!isExists) {
            throw new RuntimeException("User not found");
        }

        List<FollowEntity> followEntities = followRepository.findByIdFollowingId(userId);
        return followEntities.stream()
                .map(follow -> new Follow(follow.getFollower(), null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Follow> getFollowing(Long userId) {

        boolean isExists = userRepository.existsById(userId);
        if (!isExists) {
            throw new RuntimeException("User not found");
        }

        List<FollowEntity> followEntities = followRepository.findByIdFollowerId(userId);
        return followEntities.stream()
                .map(follow -> new Follow(null, follow.getFollowing()))
                .toList();
    }

    @Transactional
    public void follow(Long userId, Long followId) {

        if(followRepository.existsByIdFollowerIdAndIdFollowingId(userId, followId)) {
            throw new RuntimeException("Is Already Follower");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserEntity followEntity = userRepository.findById(followId)
                .orElseThrow(() -> new RuntimeException("Follow not found"));

        followRepository.save(
                FollowEntity.builder()
                        .id(new FollowId(userId, followId))
                        .follower(userEntity)
                        .following(followEntity)
                        .build()
        );
    }

    @Transactional
    public void unfollow(Long userId, Long unfollowId) {

        if(!followRepository.existsByIdFollowerIdAndIdFollowingId(userId, unfollowId)) {
            throw new RuntimeException("Is Not Follower");
        }

        followRepository.deleteById(new FollowId(userId, unfollowId));
    }
}
