package com.edurican.flint.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QTopicEntity is a Querydsl query type for TopicEntity
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopicEntity extends EntityPathBase<TopicEntity> {

    private static final long serialVersionUID = 1937169532L;

    public static final QTopicEntity topicEntity = new QTopicEntity("topicEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath topicName = createString("topicName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTopicEntity(String variable) {
        super(TopicEntity.class, forVariable(variable));
    }

    public QTopicEntity(Path<? extends TopicEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTopicEntity(PathMetadata metadata) {
        super(TopicEntity.class, metadata);
    }

}

