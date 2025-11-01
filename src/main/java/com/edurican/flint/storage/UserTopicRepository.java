package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTopicRepository extends JpaRepository<UserTopicEntity, Long> {

    List<UserTopicEntity> findByUserIdOrderByScoreDesc(Long userId);

    UserTopicEntity findByUserIdAndTopicId(Long userId, Long topicId);

    @Modifying
    @Query("UPDATE UserTopicEntity ut SET ut.score = ut.score + 1 WHERE ut.userId = :userId AND ut.topicId = :topicId")
    int incrementScore(@Param("userId") Long  userId, @Param("topicId") Long topicId);
}
