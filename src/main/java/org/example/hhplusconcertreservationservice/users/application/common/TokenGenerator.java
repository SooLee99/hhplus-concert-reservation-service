package org.example.hhplusconcertreservationservice.users.application.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 *  60 * 60 * 24)) // 15분 만료
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
