package com.gestion.eventos.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Nueva importación para la excepción de firma
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey; // Importar SecretKey para el cast en verifyWith

@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;





private SecretKey getSigningKey (){

    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}

public String generateToken (Authentication authentication){
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + jwtExpiration);
    String token = Jwts.builder()
    .subject(username)
    .issuedAt(new Date())
    .expiration(expireDate)
    .signWith(getSigningKey(),Jwts.SIG.HS512)
    .compact();
    return token;
}


public String getUsernameFromJwt (String token){

    Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();

    return claims.getSubject();
}

 public boolean validateToken(String token) {
        try {
            // La forma moderna para validación también
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token); // Aquí solo nos interesa que no lance excepción
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        } catch (SignatureException e) { // Importante para HS512
            System.out.println("Signature validation failed: " + e.getMessage());
        }
        return false;
    }


}
