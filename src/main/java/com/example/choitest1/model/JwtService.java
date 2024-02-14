package com.example.choitest1.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.security.Key;
import java.util.Date;

@Log4j2
public class JwtService {
    private final String secret = "Reb!u^sBeFr!QScFtHmKoPlH#6*$7%*(&)6_";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String createToken(String subject, int userAccessTokenTime) {
        String jws = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date((new Date()).getTime() + userAccessTokenTime))
                .signWith(key)
                .compact();
        return jws;
    }

    public String createRefreshToken(String subject, int userRefreshTokenTime) {
        String jws = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date((new Date()).getTime() + userRefreshTokenTime))
                .signWith(key)
                .compact();
        return jws;
    }

    public Jws<Claims> verifyToken(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return jws;
    }

    public long getRemainingSeconds(String token) throws JwtException {
        Jws<Claims> jws = verifyToken(token);
        long difference = jws.getBody().getExpiration().getTime() - System.currentTimeMillis();
        return difference / 1000; // 초 단위로 변환
    }

    public String TransformUserToStr(User user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(user);
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.toString();
    }
}