package com.edurican.flint.core.domain;

import com.edurican.flint.storage.BaseEntity;
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
public class UserTopic extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "score")
    private Integer score = 0;

    public UserTopic(Long userId, Long topicId) {
        this.userId = userId;
        this.topicId = topicId;
        this.score = 0;
    }

    /**
     *  특정 토픽 선호점수 증가
     */
    public void increaseScore() {
        this.score += 1;
    }

    /**
     *  특정 토픽 선호점수 감소
     */
    public void decreaseScore() {
        this.score -= 1;
    }
}
