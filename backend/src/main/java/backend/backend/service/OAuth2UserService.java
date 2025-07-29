package backend.backend.service;


import backend.backend.dto.auth.AuthProvider;
import backend.backend.dto.auth.CustomOAuth2User;
import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.OAuth2LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        // Lấy thông tin từ Google
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        OAuth2LoginRequestDto loginRequestDto = new OAuth2LoginRequestDto();
        loginRequestDto.setEmail(email);
        loginRequestDto.setName(name);
        loginRequestDto.setAuthProvider(AuthProvider.GOOGLE);
        JwtResultDto jwtResultDto = authService.loginOAuth2(loginRequestDto);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("token", jwtResultDto);
        attributes.put("username", name);
        return new CustomOAuth2User(attributes);
    }
}
