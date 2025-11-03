package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.CommentSearchRequest;
import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.api.controller.v1.response.CommentSearchResponse;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
        userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        // 부모 댓글 검증
        Long parentCommentId = request.getParentId();
        String content = request.getContent();

        if (content == null || content.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CONTENT); //
        }

        if (parentCommentId == null) {
            CommentEntity comment = new CommentEntity(userId, postId, null, content);
        }
        if (parentCommentId != null) {
            CommentEntity parent = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

            if (!parent.isActive()) throw new CoreException(ErrorType.COMMENT_NOT_FOUND);

            // 부모 댓글이 같은 게시글에 속해야 함
            if (!parent.getPostId().equals(postId))
                throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        Long grandParentId = commentRepository.findParentIdById(parentCommentId);
        if (grandParentId != null) {
            // grandParent의 parentId 조회
            Long greatGrandParent = commentRepository.findParentIdById(grandParentId);
            if (greatGrandParent != null) {
                throw new CoreException(ErrorType.COMMENT_DEPTH_EXCEEDED);
            }
        }

        // 댓글 생성, 저장
        CommentEntity comment = new CommentEntity(userId, postId, parentCommentId, request.getContent());
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                String.valueOf(comment.getUserId()),
                comment.getPostId(),
                comment.getParentCommentId(),
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
        // 핗여없음 변경감지
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

    @Transactional
    public List<CommentSearchResponse> searchComment(CommentSearchRequest request) {

        Long postId = request.getPostId();
        Long parentId = request.getParentId();

        // 게시글 ID는 필수
        if (postId == null) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        List<CommentEntity> comments;

        if (parentId == null) {
            // 최상위 댓글 조회
            comments = commentRepository.findTopLevelByPost(postId);
        } else {
            // 특정 댓글의 자식 댓글 조회 (대댓글 / 대대댓글)
            comments = commentRepository.findChildren(postId, parentId);
        }

        // CommentSearchResponse 형태로 변환
        return comments.stream()
                .map(comment -> CommentSearchResponse.builder()
                        .id(comment.getId())
                        .userId(comment.getUserId())
                        .postId(comment.getPostId())
                        .parentCommentId(comment.getParentCommentId())
                        .content(comment.getContent())
                        .likeCount(comment.getLikeCount())
                        .replyCount(commentRepository.countChildren(comment.getId())) // 대댓글 개수
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .replies(Collections.emptyList()) // 지금 단계에서는 빈 리스트로
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly=true) // readOnly=false여도 OK, 읽기만이면 readOnly=true
    public Cursor<CommentSearchResponse> getCommentsWithCursor(
            Long postId,
            Long lastFetchedId,
            Integer limit
    ) {
        if (postId == null || limit == null || limit <= 0) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        // 첫 페이지면 cursor = null
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? null : lastFetchedId;

        // limit+1로 가져와 hasNext 계산
        int fetchSize = limit + 1;
        List<CommentEntity> rows =
                commentRepository.findByPostWithCursorNative(postId, cursor, fetchSize);

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        List<CommentSearchResponse> contents = rows.stream()
                .map(c -> CommentSearchResponse.builder()
                        .id(c.getId())
                        .userId(c.getUserId())
                        .postId(c.getPostId())
                        .parentCommentId(c.getParentCommentId()) // 클라가 원하면 트리 렌더링에 사용
                        .content(c.getContent())
                        .likeCount(c.getLikeCount())
                        .replyCount(commentRepository.countChildrenNative(c.getId()))
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .replies(Collections.emptyList()) // 이 API는 평면 리스트만 전달
                        .build())
                .collect(Collectors.toList());

        Long nextCursor = contents.isEmpty()
                ? null
                : contents.get(contents.size() - 1).getId();

        return new Cursor<>(contents, nextCursor, hasNext);
    }
}
