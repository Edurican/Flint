package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Topic;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopicResponse {

    private Long id;
    private String topicName;

    public static TopicResponse from(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .build();
    }
}