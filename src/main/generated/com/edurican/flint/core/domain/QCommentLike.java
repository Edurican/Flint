package com.edurican.flint.core.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentLike is a Querydsl query type for CommentLike
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLike extends EntityPathBase<CommentLike> {

    private static final long serialVersionUID = 1027991064L;

    public static final QCommentLike commentLike = new QCommentLike("commentLike");

    public final com.edurican.flint.storage.QBaseEntity _super = new com.edurican.flint.storage.QBaseEntity(this);

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCommentLike(String variable) {
        super(CommentLike.class, forVariable(variable));
    }

    public QCommentLike(Path<? extends CommentLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentLike(PathMetadata metadata) {
        super(CommentLike.class, metadata);
    }

}

