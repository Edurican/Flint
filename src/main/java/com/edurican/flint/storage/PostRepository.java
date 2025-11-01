package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity,Long> {
    
    List<PostEntity> findByUserId(Long userId);
    List<PostEntity> findByTopicId(Long topicId);


}
