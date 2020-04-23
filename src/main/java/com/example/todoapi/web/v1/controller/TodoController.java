package com.example.todoapi.web.v1.controller;

import com.example.todoapi.service.TodoService;
import com.example.todoapi.web.v1.model.TodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TodoController.API_V_1_TODO)
@RequiredArgsConstructor
public class TodoController {

    public static final String API_V_1_TODO = "/api/v1/todo";

    private final TodoService service;

    @GetMapping
    public List<TodoDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TodoDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoDto createNew(@RequestBody TodoDto dto) {
        return service.createNew(dto);
    }

    @PutMapping("/{id}")
    public TodoDto update(@PathVariable UUID id, @RequestBody TodoDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
