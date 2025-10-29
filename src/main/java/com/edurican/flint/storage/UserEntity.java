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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "bio", length = 255, nullable = true)
    private String bio;

    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @Column(name = "followerCount", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int followersCount;

    @Column(name = "followingCount", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int followingCount;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
