package com.example.sumatra.domain.entity.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@RequiredArgsConstructor
@Setter
@Getter
@SuperBuilder
public abstract class BaseDatedEntity extends BaseIdEntity {

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @PrePersist
    protected void onPrePersist() {
        this.setCreateDate(LocalDateTime.now());
        this.setUpdateDate(LocalDateTime.now());
    }

    @PreUpdate
    protected void onPreUpdate() {
        this.setUpdateDate(LocalDateTime.now());
    }
}
