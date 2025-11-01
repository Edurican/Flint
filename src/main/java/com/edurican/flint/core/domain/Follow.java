package com.edurican.flint.core.domain;

import com.edurican.flint.storage.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {
    private Long id;
    private Long followId;
    private String username;
    private LocalDateTime followedAt;
}
