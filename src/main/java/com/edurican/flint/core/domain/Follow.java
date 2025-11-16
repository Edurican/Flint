package com.edurican.flint.core.domain;

public record Follow(
        Long followId,
        Long userId,
        String username,
        String name,
        String bio,
        Integer followersCount,
        Boolean isFollow
) {

}
