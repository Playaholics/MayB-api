package kr.mayb.controller;

import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAll;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DenyAll
@RestController
public class SecurityController {

    @GetMapping("/security/denyAll")
    public void denyAll() {
    }
    
    @PermitAll
    @GetMapping("/security/permitAll")
    public ResponseEntity<ApiResponse<String>> permitAll() {
        return Responses.ok("permitAll");
    }

    @PermitAuthenticated
    @GetMapping("/security/permitAuthenticated")
    public ResponseEntity<ApiResponse<String>> permitAuthenticated() {
        return Responses.ok("permitAuthenticated");
    }
}
