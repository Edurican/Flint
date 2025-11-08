package com.edurican.flint.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequestDto {
    private String username;
    private String bio;

}
