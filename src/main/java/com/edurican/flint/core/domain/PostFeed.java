package com.edurican.flint.core.domain;

import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.storage.PostEntity;
import com.edurican.flint.storage.PostRepository;
import com.edurican.flint.storage.UserTopicEntity;
import com.edurican.flint.storage.UserTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PostFeed {

    private final UserTopicRepository userTopicRepository;
    private final PostRepository postRepository;

    @Autowired
    public PostFeed(UserTopicRepository userTopicRepository, PostRepository postRepository) {
        this.userTopicRepository = userTopicRepository;
        this.postRepository = postRepository;
    }

    public Cursor<Post> getRecommendFeed(Long userId, Long lastFetchedId, Integer limit) {

        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;

        List<UserTopicEntity> userTopics = userTopicRepository.findByUserIdOrderByScoreDesc(userId);
        if(userTopics.isEmpty()) {

            Slice<PostEntity> postEntities = postRepository.findByWithCursor(cursor, limit);
            List<Post> posts = postEntities.stream()
                    .map(Post::of)
                    .toList();

            Long nextCursor = (posts.isEmpty()) ? null : posts.get(posts.size() - 1).getId();
            Boolean hasNext = (posts.size() == limit) ? true : false;
            return new Cursor<>(posts, nextCursor, hasNext);
        }

        double totalScore = userTopics.stream().mapToDouble(UserTopicEntity::getScore).sum();

        Map<Long, Integer> topicCounts = new HashMap<>();
        for (int i = 0; i < limit; i++) {
            double rand = ThreadLocalRandom.current().nextDouble() * totalScore;
            double cumulative = 0;

            for (UserTopicEntity topic : userTopics) {
                cumulative += topic.getScore();
                if (rand < cumulative) {
                    topicCounts.merge(topic.getTopicId(), 1, Integer::sum);
                    break;
                }
            }
        }

        List<Post> posts = topicCounts.entrySet().stream()
                .flatMap(entry -> getTopicFeed(entry.getKey(), cursor, entry.getValue()).getContents().stream())
                .sorted(Comparator.comparing(Post::getId).reversed())
                .limit(limit)
                .toList();

        Long nextCursor = (posts.isEmpty()) ? null : posts.get(posts.size() - 1).getId();
        Boolean hasNext = (posts.size() == limit) ? true : false;
        return new Cursor<>(posts, nextCursor, hasNext);
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
