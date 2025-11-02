package com.edurican.flint.core.domain;

import com.edurican.flint.storage.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;
    private String content;
    private Long userId;
    private Long topicId;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer resparkCount;
    private String status;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static Post of(PostEntity postEntity) {
        return new Post();
    }

}

