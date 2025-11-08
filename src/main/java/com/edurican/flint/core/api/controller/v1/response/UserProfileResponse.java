package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.storage.UserEntity;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private String username;
    private String bio;
    private Integer followersCount;
    private Integer followingCount;
    private Long postCount;

    public UserProfileResponse(UserEntity user, Long postCount) {
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.followersCount = user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
        this.postCount = postCount;
    }
}