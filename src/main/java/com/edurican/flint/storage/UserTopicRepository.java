package com.edurican.flint.storage;

import com.edurican.flint.core.domain.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTopicRepository extends JpaRepository<UserTopic, Long> {

    List<UserTopic> findByUserId(Long userId);

    List<UserTopic> findByUserIdOrderByScoreDesc(Long userId);

    Optional<UserTopic> findByUserIdAndTopicId(Long userId, Long topicId);
}
