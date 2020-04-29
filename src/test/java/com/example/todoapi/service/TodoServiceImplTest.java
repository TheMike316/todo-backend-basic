package com.example.todoapi.service;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.domain.User;
import com.example.todoapi.repository.TodoRepository;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.web.v1.model.TodoDto;
import com.example.todoapi.web.v1.model.mapper.TodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest //to facilitate a security context
@WithMockUser(username = TodoServiceImplTest.USERNAME)
class TodoServiceImplTest {

    public static final String USERNAME = "baumfisch";

    @MockBean
    TodoRepository repository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TodoMapper mapper;

    @SpyBean
    TodoServiceImpl service;

    UUID id;

    TodoDto dto;

    Todo entity;

    User user;

    @BeforeEach
    void setUp() {
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

        user = User.builder()
                .id(UUID.randomUUID())
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .version(1L)
                .username(USERNAME)
                .password("not a real password")
                .name("Baumi Baumfischensen")
                .todos(Collections.singletonList(entity))
                .build();

        given(mapper.todoDtoToTodo(dto)).willReturn(entity);
        given(mapper.todoToTodoDto(entity)).willReturn(dto);
    }

    @Test
    void getAll() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        var actual = service.getAll();

        assertEquals(Collections.singletonList(dto), actual);
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void getById() {
        given(repository.findByIdAndUserUsername(id, USERNAME)).willReturn(Optional.of(entity));

        var actual = service.getById(id);

        assertEquals(dto, actual);
        verify(repository, times(1)).findByIdAndUserUsername(id, USERNAME);
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
        given(repository.findById(id)).willReturn(Optional.of(entity));
        service.deleteById(id);
        verify(repository, times(1)).delete(entity);
    }
}