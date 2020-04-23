package com.example.todoapi.service;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.repository.TodoRepository;
import com.example.todoapi.web.v1.model.TodoDto;
import com.example.todoapi.web.v1.model.mapper.TodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TodoServiceImplTest {

    @Mock
    TodoRepository repository;

    @Mock
    TodoMapper mapper;

    @InjectMocks
    TodoServiceImpl service;

    UUID id;

    TodoDto dto;

    Todo entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        id = UUID.randomUUID();

        dto = TodoDto.builder()
                .id(id)
                .title("Todo One")
                .completed(false)
                .build();

        entity = Todo.builder()
                .id(id)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .version(1L)
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .build();

        given(mapper.todoDtoToTodo(dto)).willReturn(entity);
        given(mapper.todoToTodoDto(entity)).willReturn(dto);
    }

    @Test
    void getAll() {
        given(repository.findAll()).willReturn(Collections.singletonList(entity));

        var actual = service.getAll();

        assertEquals(Collections.singletonList(dto), actual);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        given(repository.findById(id)).willReturn(Optional.of(entity));

        var actual = service.getById(id);

        assertEquals(dto, actual);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void createNew() {
        given(repository.save(entity)).willReturn(entity);

        var actual = service.createNew(dto);

        assertEquals(dto, actual);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void update() {
        given(repository.findById(id)).willReturn(Optional.of(entity));
        given(repository.save(entity)).willReturn(entity);

        var actual = service.update(id, dto);

        assertEquals(actual, dto);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void deleteById() {
        service.deleteById(id);
        verify(repository, times(1)).deleteById(id);
    }
}