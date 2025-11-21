package com.edurican.flint.core.domain;

import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostFeed {

    private final UserTopicRepository userTopicRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public PostFeed(UserTopicRepository userTopicRepository, PostRepository postRepository, 
                    UserRepository userRepository, TopicRepository topicRepository) {
        this.userTopicRepository = userTopicRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    public Cursor<Post> getRecommendFeed(Long userId, Long lastFetchedId, Integer limit) {

        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;

        List<UserTopicEntity> userTopics = userTopicRepository.findByUserIdOrderByScoreDesc(userId);
        double totalScore = userTopics.stream().mapToDouble(UserTopicEntity::getScore).sum();
        if(userTopics.isEmpty() || userTopics.size() < 3 || totalScore < 20.0) {    // 임시 (데이터가 쌓이지 않았다면)

            Slice<PostEntity> postEntities = postRepository.findByWithCursor(cursor, limit);
            List<PostEntity> postEntityList = postEntities.getContent();
            
            // username과 topicName 매핑
            List<Long> userIds = postEntityList.stream().map(PostEntity::getUserId).distinct().toList();
            List<Long> topicIds = postEntityList.stream().map(PostEntity::getTopicId).distinct().toList();
            
            Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));
            Map<Long, TopicEntity> topicMap = topicRepository.findAllById(topicIds).stream()
                    .collect(Collectors.toMap(TopicEntity::getId, Function.identity()));
            
            List<Post> posts = postEntityList.stream()
                    .map(postE -> {
                        User userE = userMap.get(postE.getUserId());
                        TopicEntity topicE = topicMap.get(postE.getTopicId());
                        String username = (userE != null) ? userE.getUsername() : "";
                        String topicName = (topicE != null) ? topicE.getTopicName() : "";
                        return Post.of(postE, username, topicName);
                    })
                    .toList();

            Long nextCursor = (posts.isEmpty()) ? null : posts.get(posts.size() - 1).getId();
            Boolean hasNext = (posts.size() == limit) ? true : false;
            return new Cursor<>(posts, nextCursor, hasNext);
        }

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
        List<PostEntity> postEntityList = postEntities.getContent();
        
        // username과 topicName 매핑
        List<Long> userIds = postEntityList.stream().map(PostEntity::getUserId).distinct().toList();
        List<Long> topicIds = postEntityList.stream().map(PostEntity::getTopicId).distinct().toList();
        
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, TopicEntity> topicMap = topicRepository.findAllById(topicIds).stream()
                .collect(Collectors.toMap(TopicEntity::getId, Function.identity()));
        
        List<Post> posts = postEntityList.stream()
                .map(postE -> {
                    User userE = userMap.get(postE.getUserId());
                    TopicEntity topicE = topicMap.get(postE.getTopicId());
                    String username = (userE != null) ? userE.getUsername() : "";
                    String topicName = (topicE != null) ? topicE.getTopicName() : "";
                    return Post.of(postE, username, topicName);
                })
                .toList();

        Long nextCursor = (postEntityList.isEmpty()) ? null : postEntityList.get(postEntityList.size() - 1).getId();
        Boolean hasNext = (postEntityList.size() == limit) ? true : false;

        return new Cursor<>(posts, nextCursor, hasNext);
    }

}
