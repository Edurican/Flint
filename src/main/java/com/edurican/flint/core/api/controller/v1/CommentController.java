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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
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
    public ApiResult<CommentResponse> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request) {

        Long authenticatedUserId = userDetails.getUser().getId();
        return ApiResult.success(
                CommentResponse.fromEntity(
                        commentService.createComment(authenticatedUserId, postId, userDetails.getUsername(), request)
                )
        );
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
    public ApiResult<CommentResponse> updateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        CommentResponse updateResponse = commentService.updateComment(userDetails.getUser().getId(), commentId, request);
        return ApiResult.success(updateResponse);
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
        commentService.deleteComment(userDetails.getUser().getId(), commentId);
        return ApiResult.success(null);
    }
    /**
     * 댓글 좋아요
     */
    @PostMapping("/api/v1/comment/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "다시 누르면 취소 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResult<Boolean> likeComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId) {
        Long authenticatedUserId = userDetails.getUser().getId();
        boolean liked = commentService.likeComment(authenticatedUserId, commentId);
        return ApiResult.success(liked);
    }

    /**
    *댓글 조회
    */
    @GetMapping("/api/v1/comments")
    @Operation(summary = "댓글 조회", description = "게시글의 모든 댓글을 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursorResponse.class)))
    })
    public ApiResult<CursorResponse<CommentSearchResponse>> getComments(
            @RequestParam @NotNull Long postId,
            @RequestParam(required = false) Long commentId,
            @RequestParam(required = false) Long lastFetchedId,
            @RequestParam(defaultValue = "20")
            @PositiveOrZero Integer limit
    ) {
        Cursor<CommentSearchResponse> cur = commentService.getComment(postId, commentId, lastFetchedId, limit);
        CursorResponse<CommentSearchResponse> body = new CursorResponse<>(cur.getContents(), cur.getLastFetchedId(), cur.getHasNext());
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
            @RequestParam Long postId) {
        long total = commentService.countCommentsByPost(postId);
        return ApiResult.success(total);
    }
}