package com.edurican.flint.core.api.controller.v1.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProfileImageUpdateRequestDto {

    private String imagePath;
}
