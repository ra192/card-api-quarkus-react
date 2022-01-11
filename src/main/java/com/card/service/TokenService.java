package com.card.service;

import com.card.entity.Merchant;
import com.card.service.exception.TokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@ApplicationScoped
public class TokenService {
    private final Logger logger = Logger.getLogger(TokenService.class);

    @ConfigProperty(name = "com.card.auth.jwt.secret")
    String jwtSecretKey;

    @ConfigProperty(name = "com.card.auth.jwt.expiration.minutes")
    Long expirationInMinutes;

    public Uni<String> create(Merchant merchant, String secret) {
        try {
            if (!merchant.getSecret().equalsIgnoreCase(sha256Hash(secret)))
                return Uni.createFrom().failure(new TokenException("Secret is not valid"));
        } catch (NoSuchAlgorithmException e) {
            return Uni.createFrom().failure(new TokenException("couldn't calculate hash"));
        }

        final var token = Jwts.builder().setSubject(String.valueOf(merchant.getId()))
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(expirationInMinutes)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSecret()).compact();

        logger.info("Token was created");

        return Uni.createFrom().item(token);
    }

    public Uni<Long> validate(String token) {
        final var claimsJws = Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(token);

        return Uni.createFrom().item(Long.valueOf(claimsJws.getBody().getSubject()));
    }

    private SecretKey getSecret() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String sha256Hash(String text) throws NoSuchAlgorithmException {
        final var digest = MessageDigest.getInstance("SHA-256");
        return new String(Base64.getEncoder().encode(digest.digest(text.getBytes(StandardCharsets.UTF_8))),
                StandardCharsets.UTF_8);
    }
}
