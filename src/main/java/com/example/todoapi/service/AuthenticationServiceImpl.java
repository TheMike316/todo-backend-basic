package com.example.todoapi.service;

import com.example.todoapi.config.jwt.JwtTokenProvider;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.web.v1.model.UserDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public UserDto authenticate(@NonNull String username, @NonNull String password) {
        // this will throw an exception if the credentials are bad and spring deals with the response status
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        return userRepository.findByUsername(username)
                .map(u -> UserDto.builder()
                        .name(u.getName())
                        .username(u.getUsername())
                        .token(tokenProvider.generateToken(authentication))
                        .build())
                .orElseThrow(() -> new IllegalStateException("User should be present!"));
    }
}
