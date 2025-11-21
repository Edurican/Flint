package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentLikeEntity is a Querydsl query type for CommentLikeEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLikeEntity extends EntityPathBase<CommentLikeEntity> {

    private static final long serialVersionUID = 964387107L;

    public static final QCommentLikeEntity commentLikeEntity = new QCommentLikeEntity("commentLikeEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCommentLikeEntity(String variable) {
        super(CommentLikeEntity.class, forVariable(variable));
    }

    public QCommentLikeEntity(Path<? extends CommentLikeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentLikeEntity(PathMetadata metadata) {
        super(CommentLikeEntity.class, metadata);
    }

}

