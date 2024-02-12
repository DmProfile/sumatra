package com.example.sumatra.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final String secretKey;

    private Key getSigningKey() {
        String secret = secretKey;
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extract the JWT token without "Bearer "

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                JwtContextHolder.setClaims(claims);
                filterChain.doFilter(request, response); // Allow request to proceed if token is valid
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Return 403 Forbidden if token is invalid
            } finally {
                JwtContextHolder.clear(); // Clear claims after response
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 Unauthorized if no token in the header
        }
    }
}
