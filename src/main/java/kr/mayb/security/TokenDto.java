package kr.mayb.security;

public record TokenDto(
        String token,
        int expiresIn
) {
    public static TokenDto of(String token, int expiresIn) {
        return new TokenDto(token, expiresIn);
    }
}
