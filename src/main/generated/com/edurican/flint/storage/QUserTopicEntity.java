package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserTopicEntity is a Querydsl query type for UserTopicEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserTopicEntity extends EntityPathBase<UserTopicEntity> {

    private static final long serialVersionUID = 1357940401L;

    public static final QUserTopicEntity userTopicEntity = new QUserTopicEntity("userTopicEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserTopicEntity(String variable) {
        super(UserTopicEntity.class, forVariable(variable));
    }

    public QUserTopicEntity(Path<? extends UserTopicEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserTopicEntity(PathMetadata metadata) {
        super(UserTopicEntity.class, metadata);
    }

}

