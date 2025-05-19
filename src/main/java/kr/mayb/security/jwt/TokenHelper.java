package kr.mayb.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.mayb.data.model.Authority;
import kr.mayb.data.model.Member;
import kr.mayb.data.model.RefreshToken;
import kr.mayb.data.repository.RefreshTokenRepository;
import kr.mayb.security.TokenDto;
import kr.mayb.security.TokenInvalidException;
import kr.mayb.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenHelper {
    private static final String ISSUER = "mayb.kr";

    private static final int ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000; // 15m
    private static final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000; // 1w

    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey accessKey;
    private SecretKey refreshKey;
    private JwtParser accessParser;
    private JwtParser refreshParser;

    public TokenDto generateAccessToken(Member member) {
        Claims claims = generateAccessTokenClaims(member);

        return generateJwt(claims, ACCESS_TOKEN_EXPIRY, accessKey);
    }

    @Transactional
    public TokenDto generateRefreshToken(Member member) {
        Claims claims = getRefreshTokenClaims(member);

        TokenDto tokenDto = generateJwt(claims, REFRESH_TOKEN_EXPIRY, refreshKey);

        saveRefreshToken(member, tokenDto);

        return tokenDto;
    }

    private void saveRefreshToken(Member member, TokenDto tokenDto) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setMemberId(member.getId());
        refreshToken.setToken(tokenDto.token());

        refreshTokenRepository.save(refreshToken);
    }

    private Claims generateAccessTokenClaims(Member member) {
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(member.getId()));
        claims.put("id", member.getId());
        claims.put("name", member.getName());
        claims.put("roles", member.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
        claims.put("type", "access");
        return claims;
    }

    private Claims getRefreshTokenClaims(Member member) {
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(member.getId()));
        claims.put("id", member.getId());
        claims.put("type", "refresh");
        return claims;
    }

    private TokenDto generateJwt(Claims claims, int expiry, SecretKey key) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expiry))
                .signWith(key)
                .compact();

        return TokenDto.of(jwt, expiry);
    }

    public Optional<Claims> getAccessClaims() {
        return getAccessToken()
                .map(accessToken -> getAllClaimsFromToken(accessParser, accessToken));
    }

    public Claims getRefreshClaims(String refreshToken) {
        return getAllClaimsFromToken(refreshParser, refreshToken);
    }

    private Claims getAllClaimsFromToken(JwtParser parser, String token) {
        try {
            return parser
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("Authentication failed. Invalid JWT token: {}, {}", token, e.getMessage());
            throw new TokenInvalidException("Authentication failed. Invalid JWT token: ", token, e.getMessage());
        }
    }

    private Date generateExpirationDate(long expiry) {
        return new Date(System.currentTimeMillis() + expiry);
    }

    private Optional<String> getAccessToken() {
        return HttpUtils.getCurrentRequest()
                .map(request -> request.getHeader("Authorization"))
                .filter(header -> StringUtils.startsWith(header, "Bearer "))
                .map(header -> header.substring(7));
    }

    @Value("${jwt.secret.access}")
    private void setAccessKey(String accessSecret) {
        byte[] secret = Decoders.BASE64.decode(accessSecret);
        SecretKey accessKey = Keys.hmacShaKeyFor(secret);
        this.accessKey = accessKey;
        this.accessParser = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build();
    }

    @Value("${jwt.secret.refresh}")
    private void setRefreshKey(String refreshSecret) {
        byte[] secret = Decoders.BASE64.decode(refreshSecret);
        SecretKey refreshKey = Keys.hmacShaKeyFor(secret);
        this.refreshKey = refreshKey;
        this.refreshParser = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build();
    }

    public Optional<RefreshToken> findRefreshToken(long memberId, String refreshToken) {
        return refreshTokenRepository.findByMemberIdAndToken(memberId, refreshToken);
    }

    @Transactional
    public void removeRefreshToken(long memberId, String refreshToken) {
        RefreshToken token = findRefreshToken(memberId, refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Authentication failed. Invalid refresh token"));

        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
