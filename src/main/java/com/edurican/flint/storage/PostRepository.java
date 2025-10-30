package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity,Long> {
    
    List<PostEntity> findByUser_Id(Long userId);
    List<PostEntity> findByTopic_Id(Long topicId);


}
