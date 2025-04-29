package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.mayb.dto.MemberDto;
import kr.mayb.facade.MemberFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "유저 관련 API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @Operation(summary = "유저 프로필 이미지 업데이트")
    @PermitAuthenticated
    @PutMapping("/members/profile")
    public ResponseEntity<ApiResponse<MemberDto>> updateProfile(@RequestParam("profile") MultipartFile file) {
        MemberDto response = memberFacade.updateProfile(file);
        return Responses.ok(response);
    }

}
