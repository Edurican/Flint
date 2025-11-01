package com.edurican.flint.core.domain;

import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.storage.PostRepository;
import com.edurican.flint.storage.UserTopicEntity;
import com.edurican.flint.storage.UserTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostFeed {

    private final UserTopicRepository userTopicRepository;
    private final PostRepository postRepository;

    @Autowired
    public PostFeed(UserTopicRepository userTopicRepository, PostRepository postRepository) {
        this.userTopicRepository = userTopicRepository;
        this.postRepository = postRepository;
    }

    public Cursor<Post> getRecommendFeed(Long userId) {
        List<UserTopicEntity> userTopics =  userTopicRepository.findByUserIdOrderByScoreDesc(userId);
        
    }

    public Cursor<Post> getTopicFeed(Long topicId) {

    }
}
