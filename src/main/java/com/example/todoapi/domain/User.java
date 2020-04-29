package com.example.todoapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String name;

    private String username;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;

    @Builder
    public User(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String name, String username,
                String password, List<Todo> todos) {
        super(id, version, createdDate, lastModifiedDate);
        this.name = name;
        this.username = username;
        this.password = password;
        setTodos(todos);
    }

    public void setTodos(List<Todo> todos) {
        Optional.ofNullable(todos)
                .ifPresentOrElse(
                        list -> {
                            this.todos = list;
                            // to properly store the foreign key
                            list.forEach(t -> t.setUser(this));
                        },
                        () -> this.todos = Collections.emptyList());
    }
}
