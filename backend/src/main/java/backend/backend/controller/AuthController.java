package backend.backend.controller;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.RegisterRequestDto;
import backend.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public JwtResultDto register(@RequestBody RegisterRequestDto requestDto) {
        return authService.register(requestDto);
    }

}
