package com.example.todoapi.controller;

import com.example.todoapi.service.TodoService;
import com.example.todoapi.web.v1.controller.TodoController;
import com.example.todoapi.web.v1.model.TodoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @MockBean
    TodoService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    TodoDto dto;

    UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        dto = TodoDto.builder()
                .id(id)
                .title("Todo One")
                .completed(false)
                .build();
    }

    @Test
    void getAll() throws Exception {
        given(service.getAll()).willReturn(Collections.singletonList(dto));

        mockMvc.perform(get(TodoController.API_V_1_TODO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].title").value(dto.getTitle()))
                .andExpect(jsonPath("$[0].completed").value(dto.isCompleted()));

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        given(service.getById(id)).willReturn(dto);

        mockMvc.perform(get(TodoController.API_V_1_TODO + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.completed").value(dto.isCompleted()));

        verify(service, times(1)).getById(id);
    }

    @Test
    void createNew() throws Exception {
        given(service.createNew(dto)).willReturn(dto);
        var json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(TodoController.API_V_1_TODO)
                //@Formatter:off
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                //@Formatter:on
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.completed").value(dto.isCompleted()));

        verify(service, times(1)).createNew(dto);
    }

    @Test
    void update() throws Exception {
        given(service.update(id, dto)).willReturn(dto);
        var json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(TodoController.API_V_1_TODO + "/{id}", id)
                //@Formatter:off
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                //@Formatter:on
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.completed").value(dto.isCompleted()));

        verify(service, times(1)).update(id, dto);
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(TodoController.API_V_1_TODO + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(id);
    }

}