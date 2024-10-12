package com.project.order_service.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenValidation {

    private final String JWT_SECRET = "e33cf36326dca255d146ff093c6588e9f7886776341acd20ac8c43b6aa16bb59";

    // Create a SecretKey object from the secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Validate the JWT token and return claims
    public Claims validateToken(String token) {
        try {
             Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())  // Use the SecretKey object
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info(claims.toString());
            return claims;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token has expired", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("JWT token is unsupported", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT token is compact of handler are invalid", e);
        }

    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration().before(new Date());
    }

    // Get the username (subject) from the token
    public String getUsername(String token) {
        return validateToken(token).getSubject();
    }
}
