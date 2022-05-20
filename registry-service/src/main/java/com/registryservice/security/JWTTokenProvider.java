package com.registryservice.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWTTokenProvider {

    private final JWTconfig jwtconfig;

    public JWTTokenProvider(JWTconfig jwtconfig) {
        this.jwtconfig = jwtconfig;
    }

    public String generateToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtconfig.getExpiration() * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtconfig.getSecret().getBytes())
                .compact();
    }

    public Claims getClaimsFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(jwtconfig.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtconfig.getSecret().getBytes())
                    .parseClaimsJws(authToken);

            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
