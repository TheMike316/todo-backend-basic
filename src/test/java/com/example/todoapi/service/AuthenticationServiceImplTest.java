package com.example.todoapi.service;

import com.example.todoapi.config.jwt.JwtTokenProvider;
import com.example.todoapi.domain.User;
import com.example.todoapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuthenticationServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtTokenProvider tokenProvider;

    @Mock
    UserRepository repository;

    @InjectMocks
    AuthenticationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void authenticate() {
        String username = "baum";
        String password = "baumibaum";
        String name = "Baumi";

        User user = User.builder()
                .name(name)
                .username(username)
                .password(password)
                .build();

        String token = "this is an absolutely fake token";

        given(repository.findByUsername(username)).willReturn(Optional.of(user));
        given(tokenProvider.generateToken(any())).willReturn(token);

        var actualUserDto = service.authenticate(username, password);

        assertEquals(actualUserDto.getName(), name);
        assertEquals(actualUserDto.getUsername(), username);
        assertEquals(actualUserDto.getToken(), token);

        verify(repository, times(1)).findByUsername(username);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenProvider, times(1)).generateToken(any());
    }

    @Test
    void authenticateBadCredentials() {
        given(authenticationManager.authenticate(any())).willThrow(new BadCredentialsException("Bad Credentials!"));

        assertThrows(BadCredentialsException.class, () -> service.authenticate("user", "pw"));
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenProvider, times(0)).generateToken(any());
    }
}