package com.web.datadropapi.Config;

import com.web.datadropapi.Repositories.Entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token){
        try {
            var jwt = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return jwt.getBody().getSubject() != null;
        } catch (Exception e) { //thrown if signature doesn't match or is expired
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token){
        try {
            var jwt = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);

            return jwt.getBody().get("type", String.class).equals("refresh");
        } catch (Exception e) { //thrown if signature doesn't match or is expired
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserEntity userDetails){
        var map = new HashMap<String, Object>();
        map.put("roles", userDetails.getAuthorities());
        return generateToken(map, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserEntity userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) //5 minutes from now
                .setId(userDetails.getId().toString())
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(Long userId){
        var map = new HashMap<String, String>();
        map.put("type", "refresh");
        return Jwts.builder()
                .setClaims(map)
                .setId(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) //24 hours from now
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
