package com.edurican.flint.core.domain;

import com.edurican.flint.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "follows",
        indexes = {
                @Index(name = "idx_follower_following", columnList = "follower_id, following_id", unique = true),
                @Index(name = "idx_following", columnList = "following_id")
        }
)
public class Follow extends BaseEntity {

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "following_id", nullable = false)
    private Long followingId;
}
