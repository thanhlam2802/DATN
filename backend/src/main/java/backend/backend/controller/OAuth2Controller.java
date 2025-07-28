package backend.backend.controller;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

//    @GetMapping("/authorize/google")
//    public void redirectToGoogle(HttpServletResponse response) throws IOException {
//        String googleOAuthUrl = oAuth2Service.buildGoogleOAuthUrl();
//        response.sendRedirect(googleOAuthUrl);
//    }
//
//    @GetMapping("/callback/google")
//    public JwtResultDto handleGoogleCallback(@RequestParam("code") String code) {
//        return oAuth2Service.processGoogleOAuth2Callback(code);
//    }
}
