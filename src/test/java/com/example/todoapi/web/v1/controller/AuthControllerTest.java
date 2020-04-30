package com.example.todoapi.web.v1.controller;

import com.example.todoapi.config.jwt.JwtTokenProvider;
import com.example.todoapi.service.AuthenticationService;
import com.example.todoapi.web.v1.model.JwtRequest;
import com.example.todoapi.web.v1.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @MockBean
    JwtTokenProvider tokenProvider;

    @MockBean
    AuthenticationService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void authenticate() throws Exception {
        var token = "this is a fake token";
        var name = "User McUserface";
        var username = "user";
        var password = "pw";

        var expectedResponse = UserDto.builder()
                .name(name)
                .username(username)
                .token(token)
                .build();

        given(service.authenticate(username, password)).willReturn(expectedResponse);

        var request = objectMapper.writeValueAsString(JwtRequest.builder()
                .username(username)
                .password(password)
                .build());

        //@Formatter:off
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.token").value(token));
        //@Formatter:on

        verify(service, times(1)).authenticate(username, password);
    }
}