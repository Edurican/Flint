package com.edurican.flint.core.domain;

import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.storage.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String bio;

    private UserRoleEnum role;

    private Integer followersCount;

    private Integer followingCount;

}
