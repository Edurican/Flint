package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.storage.UserEntity;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private String username;
    private String bio;
    private Integer followersCount;
    private Integer followingCount;

    public UserProfileResponse(UserEntity user) {
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.followersCount = user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
    }
}