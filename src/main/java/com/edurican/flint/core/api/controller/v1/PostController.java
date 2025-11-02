package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.api.controller.v1.response.PostResponse;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.Post;
import com.edurican.flint.core.domain.PostService;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import com.edurican.flint.storage.PostEntity;
import com.edurican.flint.storage.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController
{
    private final PostService postService;



    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping("/api/v1/posts")
    @Operation(summary = "스파크 등록", description = "스파크(게시물) 등록")
    public ApiResult<Boolean> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @Size(max = 100) String content,
            @RequestParam Long topicId
    ) {

        Long authenticatedUserId = userDetails.getUser().getId();

        boolean creatOk = this.postService.create(authenticatedUserId, content, topicId);

        return ApiResult.success(creatOk);
    }
    @PutMapping("/api/v1/posts/{postId}")
    @Operation(summary = "스파크 수정", description = "스파크(게시물) 수정")
    public ApiResult<Boolean> updatePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam String content,
        @RequestParam Long topicId
    )
    {
        Long authenticatedUserId = userDetails.getUser().getId();
        boolean updateOk= this.postService.update(postId,authenticatedUserId,content,topicId);
        return ApiResult.success(updateOk);
    }

    @DeleteMapping("/api/v1/posts/{postId}")
    @Operation(summary = "스파크 삭제", description = "스파크(게시물) 삭제 상태 변경")
    public ApiResult<Boolean> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId
    )
    {
        Long authenticatedUserId = userDetails.getUser().getId();
        boolean deleteOk = this.postService.delete(postId, authenticatedUserId);
        return ApiResult.success(deleteOk);
    }

    @GetMapping("/api/v1/posts/{postId}")
    @Operation(summary = "특정 스파크 조회", description = "특정 스파크(게시물) 조회")
    public ApiResult<PostResponse> getPost(@PathVariable Long postId)
    {
        Post post = this.postService.getPostsById(postId);
        return ApiResult.success(PostResponse.from(post));
    }

    @GetMapping("/api/v1/posts/topic/{topicId}")
    @Operation(summary = "특정 토픽별 스파크 조회", description = "특정 토픽별 스파크(게시물) 목록 조회")
    public ApiResult<List<PostResponse>> getPostsByTopicId(@PathVariable Long topicId)
    {
        List<Post> posts = this.postService.getPostsByTopic(topicId);
        return ApiResult.success(posts.stream().map(PostResponse::from).toList());
    }

    @GetMapping("/api/v1/posts/{userId}")
    @Operation(summary = "특정 유저 스파크 조회", description = "특정 토픽별 스파크(게시물) 목록 조회")
    public ApiResult<List<PostResponse>> getPostsByUserId(@PathVariable Long userId){
        List<Post> posts = this.postService.getPostsByUserId(userId);
        return ApiResult.success(posts.stream().map(PostResponse::from).toList());
    }

    @GetMapping("/api/v1/posts")
    @Operation(summary = "전체 스파크 조회", description = "전체 스파크(게시물) 목록 조회")
    public ApiResult<List<PostResponse>> getPostAll()
    {
        List<Post> post = this.postService.getAll();
        return ApiResult.success(post.stream().map(PostResponse::from).toList());
    }



}
