package com.edurican.flint.core.domain;

import com.edurican.flint.storage.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    private UserEntity follower;
    private UserEntity following;
}
