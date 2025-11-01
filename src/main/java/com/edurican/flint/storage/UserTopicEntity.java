package com.edurican.flint.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "user_topics",
        indexes = {
                @Index(name = "idx_user_topic", columnList = "user_id, topic_id", unique = true)
        }
)
public class UserTopicEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "score")
    private Integer score = 0;

    public UserTopicEntity(Long userId, Long topicId) {
        this.userId = userId;
        this.topicId = topicId;
        this.score = 0;
    }
}
