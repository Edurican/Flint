package com.edurican.flint.storage;

import com.edurican.flint.core.domain.HotPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.apache.tomcat.util.http.Method.DELETE;
import static org.hibernate.grammars.hql.HqlParser.FROM;

public interface HotPostRepository extends JpaRepository<HotPost,Long> {


    //테스트로 10개
    @Query("SELECT postId FROM HotPost ORDER BY hotScore DESC LIMIT 10 ")
    List<Long> findHotPosts();
}
