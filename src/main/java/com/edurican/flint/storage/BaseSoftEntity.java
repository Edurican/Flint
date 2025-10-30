package com.edurican.flint.storage;

import com.edurican.flint.core.enums.EntityStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseSoftEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.ACTIVE;

    public void active() {
        status = EntityStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == EntityStatus.ACTIVE;
    }

    public void deleted() {
        status = EntityStatus.DELETED;
    }
}
