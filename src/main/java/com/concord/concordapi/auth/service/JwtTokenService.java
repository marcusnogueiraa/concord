package com.concord.concordapi.auth.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.concord.concordapi.auth.entity.UserDetailsImpl;

@Service
public class JwtTokenService {    
    @Value("${jwt.private}")
    private static String SECRET_KEY;

    @Value("${spring.application.name}")
    private static String ISSUER;

    @Value("${app.zonedatetime}")
    private static String ZONE_DATETIME;

    public String generateToken(UserDetailsImpl user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(getCreationDate())
                    .withExpiresAt(getExpirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException exc) {
            throw new JWTCreationException("Error generating token.", exc);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm).withIssuer(ISSUER).build().verify(token);
            return true;
        } catch (JWTVerificationException exc) {
            return false;
        }
    }

    public String getSubjectFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
    }

    private Instant getCreationDate(){
        return ZonedDateTime.now(ZoneId.of(ZONE_DATETIME)).toInstant();
    }

    private Instant getExpirationDate(){
        return ZonedDateTime.now(ZoneId.of(ZONE_DATETIME)).plusHours(6).toInstant();
    }


}