package com.edurican.flint.storage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<PostEntity,Long> {


}
