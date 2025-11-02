package com.edurican.flint.core.api.controller.v1.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String token;
    private String username;

    public LoginResponseDto(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
