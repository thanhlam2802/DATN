package backend.backend.service;


import backend.backend.dto.auth.AuthProvider;
import backend.backend.dto.auth.CustomOAuth2User;
import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.OAuth2LoginRequestDto;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        try {
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
            attributes.put("email", email);
            return new CustomOAuth2User(attributes);
        } catch (Exception e) {
            // Log thông tin exception
            log.error("OAuth2 login failed: {}", e.getMessage());
            log.error("Exception class: {}", e.getClass().getSimpleName());
            log.error("Exception cause: {}", e.getCause() != null ? e.getCause().getMessage() : "null");
            
            // Tạo message với thông tin chi tiết
            String detailedMessage = e.getMessage();
            if (detailedMessage != null && detailedMessage.contains("vô hiệu hóa")) {
                detailedMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
            }
            
            log.info("Throwing OAuth2AuthenticationException with message: {}", detailedMessage);
            
            // Wrap BadRequestException trong OAuth2AuthenticationException để Spring Security có thể xử lý
            if (e instanceof BadRequestException) {
                // Tạo OAuth2AuthenticationException với message chứa thông tin từ BadRequestException
                String wrappedMessage = "OAuth2_ERROR:" + detailedMessage;
                throw new OAuth2AuthenticationException(wrappedMessage);
            } else {
                // Tạo BadRequestException mới và wrap trong OAuth2AuthenticationException
                BadRequestException badRequestException = new BadRequestException(detailedMessage, ErrorCode.AUTH_008);
                String wrappedMessage = "OAuth2_ERROR:" + detailedMessage;
                throw new OAuth2AuthenticationException(wrappedMessage);
            }
        }
    }
}
