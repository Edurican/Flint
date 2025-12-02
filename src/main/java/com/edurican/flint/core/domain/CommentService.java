package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.CreateCommentRequest;
import com.edurican.flint.core.api.controller.v1.request.UpdateCommentRequest;
import com.edurican.flint.core.api.controller.v1.response.CommentResponse;
import com.edurican.flint.core.api.controller.v1.response.CommentSearchResponse;
import com.edurican.flint.core.enums.EntityStatus;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    public CommentResponse createComment(Long userId, Long postId, String username, CreateCommentRequest request) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));
        // 부모 댓글 검증
        Long parentCommentId = request.getParentCommentId();
        int depth = 0;

        // 부모 댓글이 있는 경우
        if (parentCommentId != null) {
            Comment parent = commentRepository
                    .findByIdAndStatus(parentCommentId, EntityStatus.ACTIVE)
                    .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

            // 부모 댓글이 같은 게시글에 속한느지
            if (!parent.getPostId().equals(postId)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            int parentDepth = parent.getDepth();
            if (parentDepth >= 2) {
                throw new CoreException(ErrorType.COMMENT_DEPTH_EXCEEDED);
            }

            depth = parentDepth + 1;
        }

        // 댓글 생성, 저장
        Comment comment = new Comment(userId, postId, parentCommentId, username, depth, request.getContent());
        Comment saved = commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                comment.getUserId(),
                saved.getPostId(),
                saved.getParentCommentId(),
                saved.getUsername(),
                saved.getDepth(),
                saved.getContent(),
                saved.getLikeCount(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        // 댓글 존재 확인 및 작성자 확인
        Comment comment = commentRepository
                .findByIdAndStatus(commentId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

        if (!comment.isActive()) {
            throw new CoreException(ErrorType.COMMENT_NOT_FOUND);
        }
        // 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        comment.updateContent(request.getContent());

        return new CommentResponse(
                comment.getId(),
                comment.getUserId(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getUsername(),
                comment.getDepth(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 댓글 존재 확인 및 작성자 확인
        Comment comment = commentRepository
                .findByIdAndStatus(commentId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));
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
    public boolean likeComment(Long userId, Long commentId) {

        // 이미 좋아요한 경우 -> 취소
        int deleted = commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
        if (deleted > 0) {
            int updated = commentRepository.decrementLikeCount(commentId);
            if (updated <= 0) {
                // 동시성 이슈 등으로 실패한 경우, 최소한 롤백
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }
            return false; // 현재 상태: 좋아요 해제
        }

        // 좋아요가 안 되어 있었다면: insert 시도 + like_count + 1
        try {
            commentLikeRepository.save(new CommentLike(userId, commentId));

            int updated = commentRepository.incrementLikeCount(commentId);
            if (updated <= 0) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            return true; // 현재 상태: 좋아요됨
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // UNIQUE(user_id, comment_id) 때문에 중복 insert 시 여기로 옴
            // 이미 다른 트랜잭션에서 좋아요 처리한 상태이므로 '좋아요됨'으로 취급
            return true;
        }
    }

    @Transactional(readOnly = true)
    public Cursor<CommentSearchResponse> getComment(
            Long postId,
            Long commentId,
            Long lastFetchedId,
            Integer limit
    ) {
        // 첫 페이지면 cursor = null
        Long cursor = (lastFetchedId == null || lastFetchedId == 0) ? null : lastFetchedId;

        // limit+1로 가져와 hasNext 계산
        int fetchSize = limit + 1;
        List<Comment> rows;

        if (commentId == null) {
            // 1) 루트 댓글 조회 (depth=0), 최신순 (id DESC) + 커서
            rows = commentRepository.findRootCommentsWithCursor(postId, cursor, fetchSize);
//            log.info("=========================> {}", rows.size());
        } else {
            // 2) 특정 댓글의 자식 조회
            Comment base = commentRepository
                    .findByIdAndStatus(commentId, EntityStatus.ACTIVE)
                    .orElseThrow(() -> new CoreException(ErrorType.COMMENT_NOT_FOUND));

            if (!base.getPostId().equals(postId)) {
                throw new CoreException(ErrorType.DEFAULT_ERROR);
            }

            int baseDepth = base.getDepth();

            // depth 2면 더 이상 자식(=depth3)을 허용하지 않으니 빈 결과
            if (baseDepth >= 2) {
                return new Cursor<>(List.of(), null, false);
            }

            // 해당 댓글의 "직접 자식"만 조회 (대댓글/대대댓글)
            // postId + parentCommentId 로 필터링하는 기존 메서드 사용
            rows = commentRepository.findChildren(postId, base.getId());
        }

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        List<CommentSearchResponse> contents = rows.stream()
                .map(c -> {
                    long replyCount = 0L;

                    int depth = c.getDepth();

                    if (depth == 0) {
                        // 루트 댓글:
                        // 1) 내 바로 아래(depth1) 자식 수
                        long depth1 = commentRepository.countByParentCommentIdAndStatus(
                                c.getId(), EntityStatus.ACTIVE
                        );
                        // 2) 그 자식들의(depth1) 자식(depth2) 수
                        long depth2 = commentRepository.countDepth2ByRoot(c.getId());

                        replyCount = depth1 + depth2;
                    } else if (depth == 1) {
                        // depth1 댓글:
                        // 내 바로 아래(depth2) 자식 수만
                        replyCount = commentRepository.countByParentCommentIdAndStatus(
                                c.getId(), EntityStatus.ACTIVE
                        );
                    } else {
                        // depth2 이상은 더 내려가지 않음
                        replyCount = 0L;
                    }

                    return CommentSearchResponse.builder()
                            .commentId(c.getId())
                            .postId(c.getPostId())
                            .parentCommentId(c.getParentCommentId())
                            .depth(c.getDepth())
                            .username(c.getUsername())
                            .content(c.getContent())
                            .likeCount(c.getLikeCount())
                            .replyCount(replyCount)
                            .createdAt(c.getCreatedAt())
                            .build();
                })
                .toList();

        Long nextCursor = contents.isEmpty()
                ? null
                : contents.get(contents.size() - 1).getCommentId();

        return new Cursor<>(contents, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public long countCommentsByPost(Long postId) {
        if (postId == null) throw new CoreException(ErrorType.DEFAULT_ERROR);
        return commentRepository.countAllByPost(postId);
    }
}
