package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentSearchResponse;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.domain.CommentService;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import com.edurican.flint.core.support.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    /**
     * 댓글 등록
     */
    @PostMapping("/api/v1/{postId}/comments")
    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<Boolean> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {
        long userId = 1; // 임시 userId

            commentService.createComment(userId, postId, request);
            return ApiResult.success(true);
        }
    /**
     * 댓글 수정
     */
    @PatchMapping("/api/v1/comment/{commentId}")
    @Operation(summary = "댓글 수정", description = "기존 댓글 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<Boolean> updateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        long userId = 1; // 임시 userId
            commentService.updateComment(userId, commentId, request);
            return ApiResult.success(true);
    }
    /**
     * 댓글 삭제
     */
    @DeleteMapping("/api/v1/comment/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResult<Void> deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId) {
        long userId = 1; // 임시 userId
        commentService.deleteComment(userId, commentId);
        return ApiResult.success(null);
    }
    /**
     * 댓글 좋아요
     */
    @PostMapping("/api/v1/comment/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "다시 누르면 취소 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResult<Integer> likeComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId) {
        long userId = 1; // 임시 userId
        Integer updated = commentService.likeComment(userId, commentId);
        return ApiResult.success(updated);
    }
    /*
    * 댓글 조회
    * */
    @GetMapping("/api/v1/comments")
    @Operation(summary = "댓글 조회", description = "게시글의 모든 댓글을 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursorResponse.class)))
    })
    public ApiResult<CursorResponse<CommentSearchResponse>> getComments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long postId,
            @RequestParam(required = false) Long lastFetchedId,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        Cursor<CommentSearchResponse> cur =
                commentService.getCommentsWithCursor(postId, lastFetchedId, limit);

        CursorResponse<CommentSearchResponse> body = CursorResponse.<CommentSearchResponse>builder()
                .contents(cur.getContents())
                .lastFetchedId(cur.getLastFetchedId())
                .hasNext(cur.getHasNext())
                .build();

        return ApiResult.success(body);
    }

    @GetMapping("/api/v1/comments/{parentId}/replies")
    @Operation(summary = "대댓글 조회", description = "특정 대댓글에 달린 자식 댓글을조회")
    public ApiResult<CursorResponse<CommentSearchResponse>> getReplies(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long parentId,
            @RequestParam Long postId,
            @RequestParam(required = false) Long lastFetchedId,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        Cursor<CommentSearchResponse> cur =
                commentService.getRepliesWithCursor(postId, parentId, lastFetchedId, limit);

        CursorResponse<CommentSearchResponse> body = CursorResponse.<CommentSearchResponse>builder()
                .contents(cur.getContents())
                .lastFetchedId(cur.getLastFetchedId())
                .hasNext(cur.getHasNext())
                .build();
        return ApiResult.success(body);
    }

    @GetMapping("/api/v1/comments/count")
    @Operation(summary = "댓글 총 개수", description = "게시글에 달린 전체 댓글 수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "총 개수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ApiResult<Long> getCommentTotalCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long postId) {
        long total = commentService.countCommentsByPost(postId);
        return ApiResult.success(total);
    }
}