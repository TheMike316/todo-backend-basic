package com.example.todoapi.it;

import com.example.todoapi.config.jwt.JwtTokenProvider;
import com.example.todoapi.service.TodoService;
import com.example.todoapi.web.v1.controller.TodoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO add more test cases
@WebMvcTest(controllers = TodoController.class)
public class SecurityTest {

    //override cors config
    @TestConfiguration
    static class TestConfig {
        @Bean("corsConfig")
        WebMvcConfigurer corsConfig() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
                }
            };
        }
    }

    @MockBean
    JwtTokenProvider tokenProvider;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    TodoService todoService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void testUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/todo"))
                .andExpect(status().isForbidden());
    }

}
