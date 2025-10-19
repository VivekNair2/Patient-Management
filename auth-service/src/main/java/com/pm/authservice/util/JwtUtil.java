
package com.pm.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.management.JMException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;

    // Reads from env/property: first checks JWT_SECRET then jwt.secret
    public JwtUtil(Environment env) {
        String secret = env.getProperty("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = env.getProperty("jwt.secret");
        }

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Missing configuration: set environment variable 'JWT_SECRET' or property 'jwt.secret'");
        }

        byte[] keyBytes;
        boolean decodedAsBase64 = false;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
            // if decode yields zero length, treat as raw
            if (keyBytes.length == 0) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            } else {
                decodedAsBase64 = true;
            }
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            String hint = decodedAsBase64
                    ? "decoded base64 key is too short"
                    : "raw secret is too short";
            throw new IllegalStateException("JWT secret too short (" + keyBytes.length + " bytes) - " + hint + ". Provide a base64-encoded 256-bit key or a >=32 byte secret.");
        }

        try {
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create signing key from JWT secret: " + ex.getMessage(), ex);
        }
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);

        }
        catch (SignatureException ex) {
            throw new JwtException("Invalid JWT Signature");
        }
        catch(JwtException ex) {
            throw new JwtException("Invalid JWT Token: " + ex.getMessage());
        }
    }
}
