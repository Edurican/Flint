package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.CommentSearchRequest;
import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.api.controller.v1.response.CommentSearchResponse;
import com.edurican.flint.core.enums.EntityStatus;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.edurican.flint.core.enums.EntityStatus.ACTIVE;


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
    public CommentResponse createComment(Long userId, Long postId, CreateCommentRequest request) {
        // 유저 존재 확인
        if (request.getDepth() == null) {
            request.setDepth(0);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        // 부모 댓글 검증
        Long parentCommentId = request.getParentId();

        if (parentCommentId != null) {
            CommentEntity parent = commentRepository.findByIdAndStatus(parentCommentId, EntityStatus.ACTIVE)
                    .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

            // 부모 댓글이 같은 게시글에 속해야 함
            if (!parent.getPostId().equals(postId))
                throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        // 댓글 생성, 저장
        CommentEntity comment = new CommentEntity(userId, postId, parentCommentId,request.getDepth(), request.getContent());
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                String.valueOf(comment.getUserId()),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getDepth(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        // 댓글 존재 확인 및 작성자 확인
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        if (!comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }
        // 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        comment.updateContent(request.getContent());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 댓글 존재 확인 및 작성자 확인
        CommentEntity comment = commentRepository.findByIdAndUserId(userId, commentId);

        if (!comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }
        // 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        // Soft Delete
        comment.deleted();
    }
    /**
     * 댓글 좋아요/ 취소
     */
    @Transactional
    public int likeComment(Long userId, Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));
        if (!comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }

        if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            int deleted = commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            if (deleted <= 0) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }
        } else {
            commentLikeRepository.save(new CommentLikeEntity(userId, commentId));
        }

        int newCount = commentLikeRepository.countByCommentId(commentId);

        comment.updateLikeCount(newCount);

        return newCount;
    }

    @Transactional(readOnly=true) // readOnly=false여도 OK, 읽기만이면 readOnly=true
    public Cursor<CommentSearchResponse> getCommentsWithCursor(
            Long postId,
            Integer depth,
            Long lastFetchedId,
            Integer limit
    ) {
        if (depth == null) {
            depth = 0;
        }
        // 첫 페이지면 cursor = null
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? null : lastFetchedId;

        // limit+1로 가져와 hasNext 계산
        int fetchSize = limit + 1;
        List<CommentEntity> rows =
                commentRepository.findByPostWithCursorNative(postId, depth, cursor, fetchSize);

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        List<CommentSearchResponse> contents = rows.stream()
                .map(c -> CommentSearchResponse.builder()
                        .id(c.getId())
                        .userId(c.getUserId())
                        .postId(c.getPostId())
                        .parentCommentId(c.getParentCommentId())
                        .depth(c.getDepth())
                        .content(c.getContent())
                        .likeCount(commentLikeRepository.countByCommentId(c.getId()))
                        .replyCount(commentRepository.countChildrenNative(c.getId()))
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .replies(Collections.emptyList())
                        .build())
                .collect(Collectors.toList());

        Long nextCursor = contents.isEmpty()
                ? null
                : contents.get(contents.size() - 1).getId();

//        long totalCount = commentRepository.countAllByPost(postId);

        return new Cursor<>(contents, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public long countCommentsByPost(Long postId) {
        if (postId == null) throw new CoreException(ErrorType.DEFAULT_ERROR);
        return commentRepository.countAllByPost(postId);
    }
}
