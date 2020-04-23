package com.example.todoapi.service;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.repository.TodoRepository;
import com.example.todoapi.web.v1.model.TodoDto;
import com.example.todoapi.web.v1.model.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repository;
    private final TodoMapper mapper;

    private final Supplier<ResponseStatusException> notFoundExceptionSupplier =
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND);
 

    @Override
    public List<TodoDto> getAll() {
        return repository.findAll().stream().map(mapper::todoToTodoDto).collect(Collectors.toList());
    }

    @Override
    public TodoDto getById(UUID id) {
        return repository.findById(id).map(mapper::todoToTodoDto).orElseThrow(notFoundExceptionSupplier);
    }

    @Override
    public TodoDto createNew(TodoDto dto) {
        Todo savedEntity = repository.save(mapper.todoDtoToTodo(dto));
        return mapper.todoToTodoDto(savedEntity);
    }

    @Override
    public TodoDto update(UUID id, TodoDto dto) {
        var entity = repository.findById(id).orElseThrow(notFoundExceptionSupplier);

        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.isCompleted());

        return mapper.todoToTodoDto(repository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
