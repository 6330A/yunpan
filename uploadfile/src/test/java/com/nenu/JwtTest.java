package com.nenu;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testGen(){
        Map<String,Object> claims = new HashMap<String,Object>();
        claims.put("username","admin");
        claims.put("password","123456");

        String token = JWT.create()
                .withClaim("user", claims)     //添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*12))
                .sign(Algorithm.HMAC256("nenu"));    //添加密钥

        System.out.println(token);
    }

    @Test
    public void testParse(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7InBhc3N3b3JkIjoiMTIzNDU2IiwidXNlcm5hbWUiOiJhZG1pbiJ9LCJleHAiOjE3MTQ2ODgzMjd9.8zanm7yKBcU1fKafkLKfparAY5uN3fKrMmD1qPv3sjE";

        JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256("nenu")).build();
        DecodedJWT decodeJWT = jwtVerifier.verify(token);
        Map<String, Claim> claims = decodeJWT.getClaims();

        System.out.println(claims.get("user"));
    }
}
