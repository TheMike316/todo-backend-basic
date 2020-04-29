package com.example.todoapi.service;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.domain.User;
import com.example.todoapi.repository.TodoRepository;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.web.v1.model.TodoDto;
import com.example.todoapi.web.v1.model.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoMapper mapper;

    private final Supplier<ResponseStatusException> notFoundExceptionSupplier =
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND);


    @Override
    public List<TodoDto> getAll() {
        return userRepository.findByUsername(getCurrentUsername())
                .map(User::getTodos)
                .orElse(Collections.emptyList())
                .stream()
                .map(mapper::todoToTodoDto)
                .collect(Collectors.toList());
    }

    @Override
    public TodoDto getById(UUID id) {
        return todoRepository.findByIdAndUserUsername(id, getCurrentUsername())
                .map(mapper::todoToTodoDto)
                .orElseThrow(notFoundExceptionSupplier);
    }

    @Override
    public TodoDto createNew(TodoDto dto) {
        // user is added via mapstruct. not sure whether i like it that way, though.
        Todo entity = mapper.todoDtoToTodo(dto);
        entity.setId(null);
        var savedEntity = todoRepository.save(entity);
        return mapper.todoToTodoDto(savedEntity);
    }

    @Override
    public TodoDto update(UUID id, TodoDto dto) {
        var entity = todoRepository.findById(id).orElseThrow(notFoundExceptionSupplier);

        // someone's trying to modify another user's todos. just tell him you didn't find anything
        if (!entity.getUser().getUsername().equals(getCurrentUsername())) {
            throw notFoundExceptionSupplier.get();
        }

        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.isCompleted());

        return mapper.todoToTodoDto(todoRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        todoRepository.findById(id)
                // only delete if the item actually belongs to the user
                .filter(t -> t.getUser().getUsername().equals(getCurrentUsername()))
                .ifPresent(todoRepository::delete);
    }


    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
