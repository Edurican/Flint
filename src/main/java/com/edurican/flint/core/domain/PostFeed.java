package com.edurican.flint.core.domain;

import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.storage.PostEntity;
import com.edurican.flint.storage.PostRepository;
import com.edurican.flint.storage.UserTopicEntity;
import com.edurican.flint.storage.UserTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
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

        // 가중치를 설정한다
        double weight = 1 / userTopics.size();

        return Cursor.<Post>builder().build();
    }

    public Cursor<Post> getTopicFeed(Long topicId, Long lastFetchedId, Integer limit) {
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;

        Slice<PostEntity> postEntities = postRepository.findByTopicIdWithCursor(topicId, cursor, limit);
        List<Post> posts = postEntities.stream()
                .map(Post::of)
                .toList();

        Long nextCursor = (postEntities.getContent().isEmpty()) ? null : postEntities.getContent().get(postEntities.getContent().size() - 1).getId();
        Boolean hasNext = (postEntities.getContent().size() == limit) ? true : false;

        return new Cursor<>(posts, nextCursor, hasNext);
    }

}
