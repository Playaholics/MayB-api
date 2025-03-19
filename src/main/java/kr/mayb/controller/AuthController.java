package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.mayb.dto.AuthDto;
import kr.mayb.dto.MemberSignupRequest;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAll;
import kr.mayb.service.AuthService;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PermitAll
    @PostMapping("/auth/members")
    public ResponseEntity<ApiResponse<AuthDto>> registerMember(@RequestBody @Valid MemberSignupRequest request) {
        AuthDto response = authService.registerMember(request);
        return Responses.ok(response);
    }

    @Operation(summary = "일반 로그인")
    @PermitAll
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthDto>> login(@RequestBody @Valid LoginRequest request) {
        AuthDto response = authService.login(request.email(), request.password());
        return Responses.ok(response);
    }

    @Operation(summary = "토큰 갱신")
    @PermitAll
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<AuthDto>> refresh(@RequestBody @Valid RefreshRequest request) {
        AuthDto response = authService.refresh(request.refreshToken());
        return Responses.ok(response);
    }

    private record LoginRequest(
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password
    ) {}

    private record RefreshRequest(
            @NotBlank
            String refreshToken
    ) {}
}