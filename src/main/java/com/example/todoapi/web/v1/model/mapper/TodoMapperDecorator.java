package com.example.todoapi.web.v1.model.mapper;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.web.v1.model.TodoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

public class TodoMapperDecorator implements TodoMapper {

    private TodoMapper delegate;

    private UserRepository userRepository;

    @Autowired
    @Qualifier("delegate")
    public void setDelegate(TodoMapper delegate) {
        this.delegate = delegate;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public TodoDto todoToTodoDto(Todo todo) {
        return delegate.todoToTodoDto(todo);
    }

    @Override
    public Todo todoDtoToTodo(TodoDto dto) {
        var entity = delegate.todoDtoToTodo(dto);

        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username).orElseThrow(IllegalStateException::new);

        entity.setUser(user);

        return entity;
    }
}
