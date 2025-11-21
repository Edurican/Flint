package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QHotPostEntity is a Querydsl query type for HotPostEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotPostEntity extends EntityPathBase<HotPostEntity> {

    private static final long serialVersionUID = 1184319706L;

    public static final QHotPostEntity hotPostEntity = new QHotPostEntity("hotPostEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> computedAt = createDateTime("computedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Double> hotScore = createNumber("hotScore", Double.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QHotPostEntity(String variable) {
        super(HotPostEntity.class, forVariable(variable));
    }

    public QHotPostEntity(Path<? extends HotPostEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHotPostEntity(PathMetadata metadata) {
        super(HotPostEntity.class, metadata);
    }

}

