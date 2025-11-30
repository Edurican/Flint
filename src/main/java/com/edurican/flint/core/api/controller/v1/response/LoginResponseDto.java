package com.edurican.flint.core.api.controller.v1.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String token;
    private String username;
    private String profileImage;

    public LoginResponseDto(String token, String username, String profileImage) {
        this.token = token;
        this.username = username;
        this.profileImage = profileImage;
    }
}
