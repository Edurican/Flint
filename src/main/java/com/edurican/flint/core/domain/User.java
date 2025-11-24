package com.edurican.flint.core.domain;

import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.storage.BaseSoftEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseSoftEntity {

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "bio", length = 255, nullable = true)
    private String bio;

    @Column(name = "follower_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer followersCount = 0;

    @Column(name = "following_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer followingCount = 0;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "postcount")
    private Long postCount = 0L;

    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public void updateProfile(String newUsername, String newBio) {
        if (newUsername != null) {
            this.username = newUsername;
        }
        if (newBio != null) {
            this.bio = newBio;
        }
    }

    /*
     * 엔티티 계층에서 팔로우, 팔로잉 카운트 증감
     * */
    public void incrementFollowersCount() {
        if(this.followersCount == null) {
            this.followersCount = 0;
        }
        this.followersCount++;
    }

    public void decrementFollowersCount() {
        if (this.followersCount == null || this.followersCount <= 0) {
            this.followersCount = 0;
        } else {
            this.followersCount--;
        }
    }

    public void incrementFollowingCount() {
        if (this.followingCount == null) {
            this.followingCount = 0;
        }
        this.followingCount++;
    }

    public void decrementFollowingCount() {
        if (this.followingCount == null || this.followingCount <= 0) {
            this.followingCount = 0;
        } else {
            this.followingCount--;
        }
    }
}
