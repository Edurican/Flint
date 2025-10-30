package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentService {
    public CommentResponse createComment(CreateCommentRequest request) {
        return new CommentResponse();
    }

    public CommentResponse updateComment(UpdateCommentRequest request) {
        return new CommentResponse();
    }

    public void deleteComment(CommentResponse commentId) {
    }

    public CommentResponse likeComment(Long commentId) {
        return new CommentResponse();
    }

    public CommentResponse unlikeComment(Long commentId) {
        return new CommentResponse();
    }
}
