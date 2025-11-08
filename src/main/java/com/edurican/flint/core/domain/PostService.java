package com.edurican.flint.core.domain;

import com.edurican.flint.core.enums.EntityStatus;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PostFeed postFeed;
    private final PostLikeRepository postLikeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository, PostFeed postFeed, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.postFeed = postFeed;
        this.postLikeRepository = postLikeRepository;
    }

    @Transactional
    public boolean create(Long userId, String content, Long topicId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        PostEntity post = new PostEntity();
        post.createPost(userId, content, topicId);

        this.postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean update(Long postId, Long userId, String content, Long topicId) {
        PostEntity post = this.postRepository.findById(postId).orElseThrow();

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
        PostEntity post = this.postRepository.findById(postId).orElseThrow();

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
    public Cursor<Post> getAll(Long userId, Long lastFetchedId, Integer limit) {

        return postFeed.getRecommendFeed(userId, lastFetchedId, limit);

    }

    //단건 조회
    @Transactional
    public Post getPostsById(Long postId) {
        PostEntity post =  this.postRepository.findById(postId).orElseThrow();

        post.increaseViewCont();
        postRepository.save(post);

        UserEntity userE = this.userRepository.findById(post.getUserId()).orElse(null);
        TopicEntity topicE = this.topicRepository.findById(post.getTopicId()).orElse(null);

        String username = (userE != null) ? userE.getUsername() : "";
        String topicName = (topicE != null) ? topicE.getTopicName() : "";

        return Post.of(post,username,topicName);
    }

    //특정 유저의 게시물
    @Transactional(readOnly = true)
    public List<Post> getPostsByUserId(Long userId) {
        if (this.userRepository.findById(userId).isEmpty())
        {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        List<PostEntity> posts = postRepository.findByUserIdOrderByIdDesc(userId);
        if (posts.isEmpty()) return List.of();

        // 유저 아이디, 토픽 아이디 분리
        List<Long> userIds = posts.stream().map(PostEntity::getUserId).distinct().toList();
        List<Long> topicIds = posts.stream().map(PostEntity::getTopicId).distinct().toList();

        //유저 아이디의 유저 정보 얻기
        Map<Long, UserEntity> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        //토픽 아이디의 토픽 정보 얻기
        Map<Long, TopicEntity> topicMap = topicRepository.findAllById(topicIds).stream()
                .collect(Collectors.toMap(TopicEntity::getId, Function.identity()));

        // 도메인 변환 (add username/topicName)
        return posts.stream()
                .map(postE -> {
                    UserEntity userE = userMap.get(postE.getUserId());
                    TopicEntity topicE = topicMap.get(postE.getTopicId());
                    String username = (userE != null) ? userE.getUsername() : ""; // 실제 게터명에 맞게 수정
                    String topicName = (topicE != null) ? topicE.getTopicName() : "";     // 실제 게터명에 맞게 수정
                    return Post.of(postE, username, topicName);
                })
                .toList();

    }

    //특정 토픽의 게시물
    @Transactional(readOnly = true)
    public Cursor<Post> getPostsByTopic(Long topicId, Long lastFetchedId, Integer limit) {
        return postFeed.getTopicFeed(topicId, lastFetchedId, limit);
    }

    //좋아요
    @Transactional
    public void likePost(Long userId, Long postId) {
        // 댓글 존재 확인
        PostEntity post = this.postRepository.findById(postId).orElseThrow();

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
            postLikeRepository.save(new PostLikeEntity(userId, postId));
            postRepository.incrementLikeCount(postId);

        } catch (Exception e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    // 게시물 중 username을 가져와 그 사용자의 Id를 전달
    public List<Post> getPostsByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return getPostsByUserId(user.getId());
    }

    // 게시물 개수 카운팅
    @Transactional(readOnly = true)
    public long getPostCountByUserId(Long userId) {
        return postRepository.countByUserIdAndStatus(userId, EntityStatus.ACTIVE);
    }
}
