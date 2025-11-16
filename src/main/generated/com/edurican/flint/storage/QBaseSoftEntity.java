package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseSoftEntity is a Querydsl query type for BaseSoftEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseSoftEntity extends EntityPathBase<BaseSoftEntity> {

    private static final long serialVersionUID = 496610196L;

    public static final QBaseSoftEntity baseSoftEntity = new QBaseSoftEntity("baseSoftEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final EnumPath<com.edurican.flint.core.enums.EntityStatus> status = createEnum("status", com.edurican.flint.core.enums.EntityStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBaseSoftEntity(String variable) {
        super(BaseSoftEntity.class, forVariable(variable));
    }

    public QBaseSoftEntity(Path<? extends BaseSoftEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseSoftEntity(PathMetadata metadata) {
        super(BaseSoftEntity.class, metadata);
    }

}

