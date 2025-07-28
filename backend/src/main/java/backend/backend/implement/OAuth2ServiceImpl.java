package backend.backend.implement;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.entity.User;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.repository.UserRepository;
import backend.backend.service.OAuth2Service;
import backend.backend.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

//    @Value("${oauth2.google.client-id}")
//    private String clientId;
//
//    @Value("${oauth2.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${oauth2.google.redirect-uri}")
//    private String redirectUri;


//    @Override
//    public String buildGoogleOAuthUrl() {
//        return "https://accounts.google.com/o/oauth2/v2/auth"
//                + "?client_id=" + clientId
//                + "&redirect_uri=" + redirectUri
//                + "&response_type=code"
//                + "&scope=openid%20email%20profile"
//                + "&access_type=offline"
//                + "&prompt=consent";
//    }
//
//    @Override
//    public JwtResultDto processGoogleOAuth2Callback(String code) {
//        // 1. Exchange code for access_token
//        String tokenUrl = "https://oauth2.googleapis.com/token";
//        String body = String.format(
//                "code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
//                code, clientId, clientSecret, redirectUri
//        );
//
//        String tokenResponse = restTemplate.postForObject(tokenUrl, body, String.class);
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode tokenJson = mapper.readTree(tokenResponse);
//            String accessToken = tokenJson.get("access_token").asText();
//
//            // 2. Use access_token to fetch user info
//            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
//            String userInfoResponse = restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, String.class);
//            JsonNode userInfo = mapper.readTree(userInfoResponse);
//
//            String email = userInfo.get("email").asText();
//            String name = userInfo.get("name").asText();
//
//            // 3. Find or create user
//            Optional<User> userOpt = userRepository.findByEmail(email);
//            User user;
//            if (userOpt.isPresent()) {
//                user = userOpt.get();
//            } else {
//                user = new User();
//                user.setEmail(email);
//                user.setName(name);
//                user.setIsVerified(true);
//                user.setCreatedAt(LocalDateTime.now());
//                user.setUpdatedAt(LocalDateTime.now());
//                user = userRepository.save(user);
//            }
//
//            // 4. Issue tokens
//            JwtResultDto jwtResultDto = new JwtResultDto();
//            jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(user));
//            jwtResultDto.setRefreshToken(jwtTokenUtil.generateRefreshToken(user));
//            return jwtResultDto;
//
//        } catch (Exception e) {
//            throw new BadRequestException("Google login failed", ErrorCode.AUTH_008);
//        }
//    }
}