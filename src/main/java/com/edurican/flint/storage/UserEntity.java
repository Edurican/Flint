package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity{


    @Column(name = "user_name", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "bio", length = 255, nullable = true)
    private String bio;

    @Column(name = "follower_Count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer followersCount;

    @Column(name = "following_Count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer followingCount;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
