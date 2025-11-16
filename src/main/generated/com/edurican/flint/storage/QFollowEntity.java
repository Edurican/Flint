package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QFollowEntity is a Querydsl query type for FollowEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFollowEntity extends EntityPathBase<FollowEntity> {

    private static final long serialVersionUID = 1754559914L;

    public static final QFollowEntity followEntity = new QFollowEntity("followEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> followerId = createNumber("followerId", Long.class);

    public final NumberPath<Long> followingId = createNumber("followingId", Long.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QFollowEntity(String variable) {
        super(FollowEntity.class, forVariable(variable));
    }

    public QFollowEntity(Path<? extends FollowEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFollowEntity(PathMetadata metadata) {
        super(FollowEntity.class, metadata);
    }

}

