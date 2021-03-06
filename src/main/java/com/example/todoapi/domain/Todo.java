package com.example.todoapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Todo extends BaseEntity {

    private String title;

    private Boolean completed;

    @ManyToOne
    private User user;

    @Builder
    public Todo(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String title,
                Boolean completed, User user) {
        super(id, version, createdDate, lastModifiedDate);
        this.title = title;
        this.completed = completed;
        this.user = user;
    }
}
