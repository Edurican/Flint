package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostEntity is a Querydsl query type for PostEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostEntity extends EntityPathBase<PostEntity> {

    private static final long serialVersionUID = -914269991L;

    public static final QPostEntity postEntity = new QPostEntity("postEntity");

    public final QBaseSoftEntity _super = new QBaseSoftEntity(this);

    public final NumberPath<Integer> commentCount = createNumber("commentCount", Integer.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Integer> resparkCount = createNumber("resparkCount", Integer.class);

    //inherited
    public final EnumPath<com.edurican.flint.core.enums.EntityStatus> status = _super.status;

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QPostEntity(String variable) {
        super(PostEntity.class, forVariable(variable));
    }

    public QPostEntity(Path<? extends PostEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostEntity(PathMetadata metadata) {
        super(PostEntity.class, metadata);
    }

}

