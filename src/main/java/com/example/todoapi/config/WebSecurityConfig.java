package com.example.todoapi.config;

import com.example.todoapi.config.jwt.JwtAuthenticationFiler;
import com.example.todoapi.config.jwt.JwtTokenProvider;
import com.example.todoapi.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment env;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

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
                .disable() // we don't need csrf protection in our case because we
                            // (1) never create a session and
                            // (2) jwt practically doubles as a csrf token anyways
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/api/**").hasRole(Role.USER.name())
                .antMatchers("/actuator/**").hasRole(Role.ACTUATOR.name())
                .anyRequest().authenticated()
                    .and()
            .formLogin()
                    .disable()
            .httpBasic()
                    .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);
        //@Formatter:on

        http.addFilter(new JwtAuthenticationFiler(authenticationManager(), tokenProvider));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // expose AuthenticationManager as a Bean
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/todo/**")
                        .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
                registry.addMapping("/login")
                        .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                        .allowedMethods("POST");
                registry.addMapping("/actuator/**").allowedOrigins("*").allowedMethods("GET");
            }
        };
    }
}
