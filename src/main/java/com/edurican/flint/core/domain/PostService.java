package com.edurican.flint.core.domain;

import com.edurican.flint.core.api.controller.v1.response.PostResponse;
import com.edurican.flint.core.enums.EntityStatus;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PostFeed postFeed;
    private final PostLikeRepository postLikeRepository;
    private final HotPostRepository hotPostRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository, PostFeed postFeed, PostLikeRepository postLikeRepository, HotPostRepository hotPostRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.postFeed = postFeed;
        this.postLikeRepository = postLikeRepository;
        this.hotPostRepository = hotPostRepository;
    }

    @Transactional
    public boolean create(Long userId, String content, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        Post post = new Post();
        post.createPost(userId, content, topicId);

        this.postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean update(Long postId, Long userId, String content, Long topicId) {
        Post post = this.postRepository.findById(postId).orElseThrow();

        if (!post.getUserId().equals(userId))
        {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        post.modifyPost(content,topicId);


        postRepository.save(post);
        return true;
    }


    @Transactional
    public boolean delete(Long postId, Long userId) {
        Post post = this.postRepository.findById(postId).orElseThrow();

        if (!post.getUserId().equals(userId))
        {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        post.deleted();
        postRepository.save(post);
        return true;
    }


    //전체 게시물
    @Transactional(readOnly = true)
    public Cursor<PostResponse> getAll(Long userId, Long lastFetchedId, Integer limit) {

        return postFeed.getRecommendFeed(userId, lastFetchedId, limit);

    }

    //단건 조회
    @Transactional
    public PostResponse getPostsById(Long postId) {
        Post post =  this.postRepository.findById(postId).orElseThrow();

        post.increaseViewCont();
        postRepository.save(post);

        User userE = this.userRepository.findById(post.getUserId()).orElse(null);
        Topic topicE = this.topicRepository.findById(post.getTopicId()).orElse(null);

        String username = userE.getUsername();
        String topicName = topicE.getTopicName();

        return PostResponse.from(post,username,topicName);
    }

    //특정 유저의 게시물
    // 특정 유저의 게시물
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Post> posts = postRepository.findByUserIdOrderByIdDesc(userId);
        if (posts.isEmpty()) return List.of();

        List<Long> topicIds = posts.stream()
                .map(Post::getTopicId)
                .distinct()
                .toList();

        Map<Long, Topic> topicMap = topicRepository.findAllById(topicIds).stream()
                .collect(Collectors.toMap(Topic::getId, Function.identity()));

        return posts.stream()
                .map(post -> {
                    String username = user.getUsername();  // 어차피 같은 유저
                    String topicName = Optional.ofNullable(topicMap.get(post.getTopicId()))
                            .map(Topic::getTopicName)
                            .orElse("");
                    return PostResponse.from(post, username, topicName);
                })
                .toList();
    }


    //특정 토픽의 게시물
    @Transactional(readOnly = true)
    public Cursor<PostResponse> getPostsByTopic(Long topicId, Long lastFetchedId, Integer limit) {
        return postFeed.getTopicFeed(topicId, lastFetchedId, limit);
    }

    //좋아요
    @Transactional
    public void likePost(Long userId, Long postId) {
        // 댓글 존재 확인
        Post post = this.postRepository.findById(postId).orElseThrow();

        // 이미 좋아요한 경우 -> 좋아요 취소
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            int deleteCount = postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            if (deleteCount <= 0) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }
            PostRepository postRepository = this.postRepository;
            postRepository.decrementLikeCount(postId);
            return; // 좋아요 취소 후 종료
        }

        // 좋아요 추가
        try {
            postLikeRepository.save(new PostLike(userId, postId));
            postRepository.incrementLikeCount(postId);

        } catch (Exception e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    // 게시물 중 username을 가져와 그 사용자의 Id를 전달
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return getPostsByUserId(user.getId());
    }

    // 게시물 개수 카운팅
    @Transactional(readOnly = true)
    public long getPostCountByUserId(Long userId) {
        return postRepository.countByUserIdAndStatus(userId, EntityStatus.ACTIVE);
    }

    //핫 게시물
    @Transactional(readOnly = true)
    public List<PostResponse> getHotPosts() {
        List<Long> hotId = hotPostRepository.findHotPosts();
        if (hotId.isEmpty()) return List.of();

        List<Post> posts = postRepository.findAllById(hotId);
        if (posts.isEmpty()) return List.of();

        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        List<Long> userIds  = posts.stream().map(Post::getUserId).distinct().toList();
        List<Long> topicIds = posts.stream().map(Post::getTopicId).distinct().toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Map<Long, Topic> topicMap = topicRepository.findAllById(topicIds).stream()
                .collect(Collectors.toMap(Topic::getId, Function.identity()));

        return hotId.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(post -> {
                    String username  = Optional.ofNullable(userMap.get(post.getUserId()))
                            .map(User::getUsername).orElse("");

                    String topicName = Optional.ofNullable(topicMap.get(post.getTopicId()))
                            .map(Topic::getTopicName).orElse("");

                    return PostResponse.from(post, username, topicName);
                })
                .toList();
    }

}
