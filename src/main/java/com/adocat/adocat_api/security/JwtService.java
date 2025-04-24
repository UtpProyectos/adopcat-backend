package com.adocat.adocat_api.security;

import com.adocat.adocat_api.domain.entity.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 86400000; // 1 día
    private final Key secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().getRoleName()) // Ej: ROLE_ADMIN
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return getParser().parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractRole(String token) {
        return getParser().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    private JwtParser getParser() {
        return Jwts.parser().verifyWith((javax.crypto.SecretKey) secretKey).build();
    }

}
