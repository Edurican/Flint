package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.PostResponse;
import com.edurican.flint.core.domain.Post;
import com.edurican.flint.core.support.Cursor;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PostRepositoryCustom {

    /**
     *  선호하는 게시글 검색
     */
    List<PostResponse> findPreferredPosts(List<Long> preferredTopicIds, Long userId, Long lastFetchedId, int limit);

    /**
     *  추천하는 게시글 검색
     */
    List<PostResponse> findRecommendPosts(List<Long> excludingPostIds, Long userId, Long lastFetchedId, int limit);

    /**
     *  특정 토픽 게시글 검색
     */
    List<PostResponse> findByTopicIdWithCursor(Long topicId, Long userId, Long lastFetchedId, int limit);
}
