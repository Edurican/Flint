package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -941269052L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QBaseSoftEntity _super = new QBaseSoftEntity(this);

    public final StringPath bio = createString("bio");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Integer> followersCount = createNumber("followersCount", Integer.class);

    public final NumberPath<Integer> followingCount = createNumber("followingCount", Integer.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final NumberPath<Long> postCount = createNumber("postCount", Long.class);

    public final EnumPath<com.edurican.flint.core.enums.UserRoleEnum> role = createEnum("role", com.edurican.flint.core.enums.UserRoleEnum.class);

    //inherited
    public final EnumPath<com.edurican.flint.core.enums.EntityStatus> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

