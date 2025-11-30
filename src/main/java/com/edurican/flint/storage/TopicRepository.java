package com.edurican.flint.storage;

import com.edurican.flint.core.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic,Long> {
    List<Topic> findAllByOrderByIdAsc();

}
