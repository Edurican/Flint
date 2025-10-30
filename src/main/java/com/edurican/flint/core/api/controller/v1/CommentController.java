package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.domain.CommentService;
import com.edurican.flint.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController // 리스폰스 바디 + 컨트롤러@
@RequiredArgsConstructor // 필요한 생성자를 자동으로 만들어줌
@RequestMapping("/api/v1/comment")
public class CommentController {
    
    private final CommentService commentService; // 이렇게 DI 받는다(외워) service 매서드 불러오기.

    // 댓글 등록
    @PostMapping
    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<CommentResponse> createComment(@RequestBody CreateCommentRequest request) {
        CommentResponse result = commentService.createComment(request);
        return ApiResult.success(result);
    }

    // 댓글 수정
    @PatchMapping
    @Operation(summary = "댓글 수정", description = "기존 댓글 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<CommentResponse> updateComment(@RequestBody UpdateCommentRequest request) {
        CommentResponse result = commentService.updateComment(request);
        return ApiResult.success(result);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content)
    })
    public ApiResult<Void> delete(CommentResponse request) {
        commentService.deleteComment(request);
        return ApiResult.success(null);
    }

    // 댓글 좋아요
    @PostMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 추가합니다.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "좋아요 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<CommentResponse> likeComment(@PathVariable Long commentId) {
        CommentResponse result = commentService.likeComment(commentId);
        return ApiResult.success(result);
    }

    // 댓글 좋아요 취소
    @DeleteMapping("/{commentId}/unlike")
    @Operation(summary = "댓글 좋아요 취소", description = "댓글의 좋아요를 취소합니다.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    })
    public ApiResult<CommentResponse> unlikeComment(@PathVariable Long commentId) {
        CommentResponse result = commentService.unlikeComment(commentId);
        return ApiResult.success(result);
    }
}
