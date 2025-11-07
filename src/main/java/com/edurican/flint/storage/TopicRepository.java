package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity,Long> {
    List<TopicEntity> findAllByOrderByIdAsc();

}
