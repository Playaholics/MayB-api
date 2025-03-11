package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.mayb.error.BadRequestException;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello", description = "Hello API")
@RestController
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return Responses.ok("hello mayb API!");
    }

    @Hidden
    @GetMapping("/hello/client/error")
    public ResponseEntity<Void> clientError() {
        throw new BadRequestException("Error!!!");
    }

    @Hidden
    @GetMapping("/hello/server/error")
    public ResponseEntity<Void> serverError() {
        throw new RuntimeException("Error!!!");
    }
}
