package com.example.todoapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment env;

    @Override
    public void configure(WebSecurity web) throws Exception {
        //strictly speaking not necessary, as dev-tools are not present in the boot jar
        if (Arrays.stream(env.getActiveProfiles()).noneMatch(p -> "staging".equals(p) || "production".equals(p))) {
            web.ignoring().antMatchers("/h2-console/**");
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@Formatter:off
        http
            .cors()
                    .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
                    .and()
            .formLogin()
                    .and()
            .httpBasic()
                    .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);
        //@Formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Bean
    WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/todo/**")
                        .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
