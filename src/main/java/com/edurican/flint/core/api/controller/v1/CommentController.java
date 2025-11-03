package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.CommentSearchRequest;
import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentSearchResponse;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.domain.CommentService;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.response.ApiResult;
import com.edurican.flint.core.support.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResult<Void> deleteComment(@PathVariable Long commentId) {
        long userId = 1; // 임시 userId
        commentService.deleteComment(userId, commentId);
        return ApiResult.success(null);
    }
    /**
     * 댓글 좋아요
     */
    @PostMapping("/api/v1/comment/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResult<Boolean> likeComment(@PathVariable Long commentId) {
        long userId = 1; // 임시 userId
        commentService.likeComment(userId, commentId);
        return ApiResult.success(true);
    }
    /**
     * 댓글 좋아요 취소
     */
    @DeleteMapping("/api/v1/comment/{commentId}/like")
    @Operation(summary = "댓글 좋아요 취소", description = "댓글의 좋아요를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResult<Boolean> unlikeComment(@PathVariable Long commentId) {
        long userId = 1; // 임시 userId
        commentService.likeComment(userId, commentId);
        return ApiResult.success(true);
    }
    
    /*
    * 댓글 조회
    * */
    /** 댓글 리스트(전 레벨) 무한 스크롤 조회: postId + lastFetchedId + limit */
    @GetMapping("/api/v1/comments")
    @Operation(summary = "댓글 조회", description = "게시글의 모든 댓글을 커서 기반으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursorResponse.class)))
    })
    public ApiResult<CursorResponse<CommentSearchResponse>> getComments(
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
}