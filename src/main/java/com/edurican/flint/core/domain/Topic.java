package com.edurican.flint.core.domain;


import com.edurican.flint.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "topics")
@NoArgsConstructor
public class Topic extends BaseEntity {

    @Column(name = "topic")
    private String topicName;

    public Topic(String topicName) {
        this.topicName = topicName;
    }
}
