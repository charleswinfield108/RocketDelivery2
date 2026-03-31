package com.rocketFoodDelivery.rocketFood.security;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserEntity user) {
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + (60 * 60 * 1000); // 1 hour expiration
        
        Date now = new Date(nowMillis);
        Date expiry = new Date(expirationMillis);
        
        String role = user.isEmployee() ? "ROLE_EMPLOYEE" : "ROLE_USER";
        
        return Jwts.builder()
                .subject(String.format("%s,%s", user.getId(), user.getEmail()))
                .claim("username", user.getEmail())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .issuer("rocketfood-app")
                .signWith(getSigningKey())
                .compact();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }
        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String getIssuer(String token) {
        return parseClaims(token).getIssuer();
    }

    public long getIssuedAt(String token) {
        return parseClaims(token).getIssuedAt().getTime();
    }

    public long getExpiresAt(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}