package com.edurican.flint.core.domain;

import com.edurican.flint.core.api.controller.v1.response.PostResponse;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostFeed {
    private final UserTopicRepository userTopicRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    /**
     *  사용자가 선호하는 토픽을 추천한다
     *  만약 데이터가 충분하지 않다면 인기글을 추천한다
     */
    public Cursor<PostResponse> getRecommendFeed(Long userId, Long lastFetchedId, Integer limit) {

        // 특정 유저의 선호 토픽을 조회후 점수 합산
        List<UserTopic> userTopics = userTopicRepository.findByUserIdOrderByScoreDesc(userId);
        int totalScore = userTopics.stream().mapToInt(UserTopic::getScore).sum();

        // 인기있는 게시글과 선호하는 게시글을 점수에따라 분배
        double recommendRatio = calculateRecommendRatio(totalScore);
        int recommendLimit = (int) (limit * (1 - recommendRatio));      // 인기있는 게시글 추천
        int preferredLimit = limit - recommendLimit;                    // 선호하는 게시글 추천

        // 선호하는 피드를 탐색
        List<Long> preferredTopicIds = userTopics.stream().map(UserTopic::getTopicId).toList();
        List<PostResponse> feed = new ArrayList<>(postRepository.findPreferredPosts(preferredTopicIds, userId, lastFetchedId, preferredLimit));

        // 만약 limit보다 개수가 낮다면 그만큼 추천 피드의 개수를 추가
        recommendLimit += preferredLimit - feed.size();

        // 선호 피드의 id를 조회하여 중복 조회되는 일을 방지
        lastFetchedId = feed.isEmpty() ? lastFetchedId : feed.get(feed.size() - 1).getId();

        // 인기있는 피드를 탐색 (선호하는 피드와 중복이 없어야함)
        List<Long> excludingPostIds = feed.stream().map(PostResponse::getId).toList();
        feed.addAll(postRepository.findRecommendPosts(excludingPostIds, userId, lastFetchedId, recommendLimit));

        return makeCursor(feed, limit);
    }

    private double calculateRecommendRatio(int totalScore) {
        if(totalScore < 5) return 0.0;      // 100% 추천 피드
        if(totalScore < 10) return 0.5;     //  50% 추천 피드
        return 0.2;                         //  20% 추천 피드
    }

    /**
     *  특정 토픽을 최신순으로 보여준다
     */
    public Cursor<PostResponse> getTopicFeed(Long topicId, Long userId, Long lastFetchedId, Integer limit) {
        return makeCursor(postRepository.findByTopicIdWithCursor(topicId, userId, lastFetchedId, limit), limit);
    }

    private Cursor<PostResponse> makeCursor(List<PostResponse> result, Integer limit) {
        boolean hasNext = result.size() > limit;
        long nextCursor = 0L;
        if(hasNext) {
            result.remove(limit.intValue());
            nextCursor = result.get(result.size() - 1).getId();
        }
        return new Cursor<>(result, nextCursor, hasNext);
    }
}
