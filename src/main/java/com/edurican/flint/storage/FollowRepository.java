package com.edurican.flint.storage;

import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.support.Cursor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom  {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    int deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
