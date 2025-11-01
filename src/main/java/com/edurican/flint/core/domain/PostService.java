package com.edurican.flint.core.domain;

import com.edurican.flint.storage.PostEntity;
import com.edurican.flint.storage.PostRepository;
import com.edurican.flint.storage.TopicRepository;
import com.edurican.flint.storage.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;


    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional
    public boolean create(Long userId, String content, Long topicId)
    {
        PostEntity post = new PostEntity();
        post.createPost(userId, content, topicId);

        this.postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean update(Long postId, Long userId, String content, Long topicId)
    {
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
    @Transactional
    public List<Post> getAll()
    {
        return this.postRepository.findAll()
                .stream()
                .map(this::toPost)
                .toList();
    }

    //단건 조회
    @Transactional
    public Post getPostsById(Long postId)
    {
        PostEntity post =  this.postRepository.findById(postId).orElseThrow();
        return toPost(post);
    }

    //특정 유저의 게시물
    @Transactional
    public List<Post> getPostsByUserId(Long userId)
    {
        if (this.userRepository.findById(userId).isEmpty())
        {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }


        return this.postRepository.findByUserId(userId)
            .stream()
            .map(this::toPost)
            .toList();
    }

    //특정 토픽의 게시물
    @Transactional
    public List<Post> getPostsByTopic(Long topicId)
    {
        return this.postRepository.findByTopicId(topicId)
                .stream()
                .map(this::toPost)
                .toList();
    }


    private Post toPost(PostEntity e)
    {
        return new Post(
                e.getId(),
                e.getContent(),
                e.getUserId(),
                e.getTopicId(),
                e.getViewCount(),
                e.getCommentCount(),
                e.getLikeCount(),
                e.getResparkCount(),
                e.getStatus().name(),
                e.getUpdatedAt(),
                e.getCreatedAt()
        );
    }


}
