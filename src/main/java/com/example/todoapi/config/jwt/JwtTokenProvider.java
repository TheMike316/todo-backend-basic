package com.example.todoapi.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String ROLES_CLAIM_NAME = "roles";
    private static final String ROLE_PREFIX = "ROLE_";
    private final JwtProperties properties;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public String generateToken(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLES_CLAIM_NAME, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(Instant.now().plusMillis(properties.getExpirationInMs())))
                .compact();
    }

    public Optional<Authentication> getAuthentication(HttpServletRequest request) {
        return resolveToken(request)
                .map(t -> {
                    var claims = parseClaims(t);
                    return Optional.ofNullable(claims.getSubject())
                            .map(username -> {
                                var authorities = Arrays.stream(claims.get(ROLES_CLAIM_NAME, String.class).split(","))
                                        .map(a -> a.startsWith(ROLE_PREFIX) ? a : ROLE_PREFIX + a)
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toSet());
                                return new UsernamePasswordAuthenticationToken(username, null, authorities);
                            }).orElse(null);
                });
    }

    public boolean validateToken(HttpServletRequest request) {
        return resolveToken(request)
                .map(this::parseClaims)
                .map(c -> Date.from(Instant.now()).before(c.getExpiration()))
                .orElse(false);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


    private Optional<String> resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(properties.getHeaderName()))
                .map(h -> h.startsWith(properties.getTokenPrefix()) ? h.substring(properties.getTokenPrefix().length() + 1) : null);
    }
}
