package com.example.todoapi.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFiler extends BasicAuthenticationFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFiler(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        super(authenticationManager);
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        tokenProvider.getAuthentication(request)
                .filter(a -> tokenProvider.validateToken(request))
                .ifPresent(a -> SecurityContextHolder.getContext().setAuthentication(a));

        chain.doFilter(request, response);
    }
}
