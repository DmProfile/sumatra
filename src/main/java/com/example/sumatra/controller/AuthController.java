package com.example.sumatra.controller;

import com.example.sumatra.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Получение токена для доступа к API")
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> generateToken(@RequestBody UserAuthDto userAuthDto) {

        Boolean existed = userRepository.existByEmailAndPassword(userAuthDto.getEmail(), userAuthDto.getPassword());

        if (!existed) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        String token = Jwts.builder()
                .claim("user_id", 12345)
                .setSubject(userAuthDto.getEmail())
                .signWith( getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    private Key getSigningKey() {
        String secret = "secret";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Schema(description = "Получение токена для доступа к API")
    @Data
    public static class UserAuthDto {
        @Schema(description = "Email пользователя")
        @Email(message = "Некорректный email")
        @NotNull(message = "Email не может быть пустым")
        private String email;

        @Schema(description = "Пароль пользователя")
        @NotNull(message = "Пароль не может быть пустым")
        @Length(min = 8, max = 500, message = "Длина пароля должна быть от 8 до 500 символов")
        private String password;
    }
}
