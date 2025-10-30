package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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
public class FollowEntity extends BaseEntity {

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "following_id", nullable = false)
    private Long followingId;
}