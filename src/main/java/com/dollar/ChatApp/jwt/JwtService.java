package com.dollar.ChatApp.jwt;

import com.dollar.ChatApp.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
//    = "dG91Y2hzdHJvbmdlcmRlZXJmYW1vdXNtb2Rlcm5zdGFnZWRvZ29yZGVyYmVzaGFsbHM=";

    private  final String SECRET_KEY = "dG91Y2hzdHJvbmdlcmRlZXJmYW1vdXNtb2Rlcm5zdGFnZWRvZ29yZGVyYmVzaGFsbHM=";


    private final Long jwtExpiration=(long) 7*24*60*60;

    public String generateToken(UserModel userModel){
        return generateToken(new HashMap<>(),userModel);
    }
    public String generateToken(Map<String,Object> extractClaims,UserModel userModel){
        Map<String,Object> claims=new HashMap<>(extractClaims);
        claims.put("userId",userModel.getId());

        return Jwts
                .builder()
                .claims(claims)
                .subject(userModel.getUserName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey(){
        byte [] bytes= Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractUserId(String token) {
        String userId= extractClaim(token,claims -> claims.get("userId", String.class));
        return userId!=null ?userId :null;
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserModel user) {
        final String userId = extractUserId(token);
        return userId.equals(user.getId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
