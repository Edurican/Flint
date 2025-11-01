package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    /**
     * 댓글 등록
     */
    @Transactional
    public void createComment(Long userId, Long postId, CreateCommentRequest request) {
        // 유저 존재 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 댓글 생성 및 저장
        CommentEntity commentEntity = new CommentEntity(userId, postId, request.getParentCommentId(), request.getContent(), 0);

        commentRepository.save(commentEntity);
    }
    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        // 댓글 존재 확인 및 작성자 확인
        CommentEntity comment = commentRepository.findByIdAndUserId(userId, commentId);

        if (comment == null || !comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }

        // 유저 정보 가져오기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        // 댓글 수정
        CommentEntity commentEntity = new CommentEntity(userId, commentId, request.getParentCommentId(), request.getContent(), 0);

        commentRepository.save(commentEntity);
    }
    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 댓글 존재 확인 및 작성자 확인
        CommentEntity comment = commentRepository.findByIdAndUserId(userId, commentId);

        if (comment == null || !comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }

        // Soft Delete
        comment.deleted();
        commentRepository.save(comment);
    }
    /**
     * 댓글 좋아요/ 취소
     */
    @Transactional
    public void likeComment(Long userId, Long commentId) {
        // 댓글 존재 확인
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

        if (!comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }

        // 이미 좋아요한 경우 -> 좋아요 취소
        if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            int deleteCount = commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            if (deleteCount <= 0) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }
            return; // 좋아요 취소 후 종료
        }

        // 좋아요 추가
        try {
            CommentLikeEntity likeEntity = new CommentLikeEntity(userId, commentId);
            commentLikeRepository.save(likeEntity);
        } catch (Exception e) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }
}
