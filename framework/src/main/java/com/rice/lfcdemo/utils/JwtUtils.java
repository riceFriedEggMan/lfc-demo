package com.rice.lfcdemo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {
    public static final Long JWT_TTL = 60 * 60 * 1000L;

    public static final String JWT_SECRET = "lfcdemo";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replace("-", "");
        return token;
    }

    public static String createJWT(String subject){
        JwtBuilder jwtBuilder = getJwtBuilder(subject, null, getUUID());
        return jwtBuilder.compact();
    }



    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = getSecretKey();
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
        if (ttlMillis == null){
            ttlMillis = JWT_TTL;
        }
        long expireMillis = currentTimeMillis + ttlMillis;
        Date expDate = new Date(expireMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("lfc")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);

    }

    public static SecretKey getSecretKey(){
        byte[] decode = Base64.getDecoder().decode(JwtUtils.JWT_SECRET);
        SecretKeySpec key = new SecretKeySpec(decode, 0, decode.length, "AES");
        return key;
    }

    public static Claims parseJWT(String jwt){
        SecretKey secretKey = getSecretKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


    public static void main(String[] args) throws Exception {
        //String token ="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjYWM2ZDVhZi1mNjVlLTQ0MDAtYjcxMi0zYWEwOGIyOTIwYjQiLCJzdWIiOiJzZyIsImlzcyI6InNnIiwiaWF0IjoxNjM4MTA2NzEyLCJleHAiOjE2MzgxMTAzMTJ9.JVsSbkP94wuczb4QryQbAke3ysBDIL5ou8fWsbt_ebg ";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlMmFkNmIwZDYyYmI0ZGVkYmU3NWNhY2JjNTBhMDdhYyIsInN1YiI6IjIwNTI5NDA1NTQ2NjIyNDAyNTgiLCJpc3MiOiJsZmMiLCJpYXQiOjE3Nzg1MDA0MTUsImV4cCI6MTc3ODUwNDAxNX0.aPiFOGDt2OEWP_3gCNhr9pkQKvc0A62Cg98bU9hVo00";
        Claims claims = parseJWT(token);
        System.out.println(claims);
    }
}
