package com.edurican.flint.storage;

import com.edurican.flint.core.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity extends BaseSoftEntity {

    @Column(name = "name")
    private String name;

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

    public UserEntity(String username, String password, String email, UserRoleEnum role) {
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
}
