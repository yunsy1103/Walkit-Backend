package com.walkit.walkit.global.security.jwt;

import com.walkit.walkit.domain.user.entity.RefreshToken;
import com.walkit.walkit.domain.user.entity.User;
import com.walkit.walkit.domain.user.repository.RefreshTokenRepository;
import com.walkit.walkit.domain.user.repository.UserRepository;
import com.walkit.walkit.global.exception.CustomException;
import com.walkit.walkit.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiryDate(LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()))
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String refreshAccessToken(String refreshToken) {
        Long userId = getUserIdFromToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (storedToken.isUsed()) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        if (storedToken.isExpired()) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return generateAccessToken(userId);
    }

    public UserPrincipal loadUserPrincipalFromToken(String token) {
        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserPrincipal.from(user);
    }
}
