package com.example.todoapi.service;


import com.example.todoapi.web.v1.model.TodoDto;

import java.util.List;
import java.util.UUID;

public interface TodoService {

    List<TodoDto> getAll();

    TodoDto getById(UUID id);

    TodoDto createNew(TodoDto dto);

    TodoDto update(UUID id, TodoDto dto);

    void deleteById(UUID id);
}
