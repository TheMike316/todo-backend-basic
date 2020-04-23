package com.example.todoapi.bootstrap;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile({"!staging", "!production"})
@RequiredArgsConstructor
public class DataBootstrap implements InitializingBean {

    private final TodoRepository repository;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (repository.count() == 0) {
            repository.saveAll(createTodos());
        }
    }

    private List<Todo> createTodos() {
        return Arrays.asList(
                Todo.builder()
                        .title("Todo One")
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Todo Two")
                        .completed(true)
                        .build(),
                Todo.builder()
                        .title("Todo Three")
                        .completed(false)
                        .build()
        );
    }
}
