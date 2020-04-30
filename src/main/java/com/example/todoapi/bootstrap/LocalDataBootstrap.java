package com.example.todoapi.bootstrap;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.domain.User;
import com.example.todoapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile({"!staging", "!production"})
@RequiredArgsConstructor
public class LocalDataBootstrap implements InitializingBean {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (repository.count() == 0) {
            repository.saveAll(createUsers());
        }
    }

    private List<User> createUsers() {
        return Arrays.asList(
                User.builder()
                        .name("Baumfisch")
                        .username("baum")
                        .password(encoder.encode("baum"))
                        .todos(createTodos1())
                        .build(),
                User.builder()
                        .name("Fischbaum")
                        .username("fisch")
                        .password(encoder.encode("fisch"))
                        .todos(createTodos2())
                        .build()
        );
    }

    private List<Todo> createTodos1() {
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

    private List<Todo> createTodos2() {
        return Arrays.asList(
                Todo.builder()
                        .title("Do this")
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Do that")
                        .completed(true)
                        .build(),
                Todo.builder()
                        .title("Do more of this")
                        .completed(false)
                        .build()
        );
    }
}
