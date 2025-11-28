package com.edurican.flint.core.domain;

import com.edurican.flint.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hot_posts",
        indexes = { @Index(name = "idx_hot_posts_id", columnList = "post_id") })
public class HotPost extends BaseEntity {

    @Column(name = "hot_score", nullable = false)
    private double hotScore;

    @Column(name = "post_id", nullable = false, unique = true)
    private long postId;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt = LocalDateTime.now();

}
