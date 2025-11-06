package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String bio;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .build();
    }
}
