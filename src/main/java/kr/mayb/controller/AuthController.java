package kr.mayb.controller;

import jakarta.validation.Valid;
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

@DenyAll
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PermitAll
    @PostMapping("/auth/members")
    public ResponseEntity<ApiResponse<AuthDto>> registerMember(@RequestBody @Valid MemberSignupRequest request) {
        AuthDto response = authService.registerMember(request);
        return Responses.ok(response);
    }

}
