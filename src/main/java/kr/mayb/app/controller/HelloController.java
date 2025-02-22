package kr.mayb.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello", description = "Hello API")
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello mayb API !";
    }
}
