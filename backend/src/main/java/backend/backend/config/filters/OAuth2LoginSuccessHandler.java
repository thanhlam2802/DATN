package backend.backend.config.filters;

import backend.backend.dto.auth.CustomOAuth2User;
import backend.backend.dto.auth.JwtResultDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.web.domain}")
    private String webDomain;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        JwtResultDto jwtResultDto = user.getAttribute("token");
        String email = user.getAttribute("email");
        assert jwtResultDto != null;
        String redirectUrl = webDomain + "/oauth2/login/success?t=" + jwtResultDto.getAccessToken() + "&rt=" + jwtResultDto.getRefreshToken() + "&email=" + email;
        response.sendRedirect(redirectUrl);
    }
}
