package backend.backend.config.filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import backend.backend.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    
    @Value("${app.web.domain}")
    private String webDomain;
    
    public OAuth2LoginFailureHandler() {
        log.info("=== OAuth2LoginFailureHandler constructor called ===");
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     AuthenticationException exception) throws IOException, ServletException {
        
        log.error("=== OAuth2LoginFailureHandler START ===");
        log.error("OAuth2 login failed: {}", exception.getMessage());
        log.error("Exception class: {}", exception.getClass().getSimpleName());
        log.error("Exception cause: {}", exception.getCause() != null ? exception.getCause().getMessage() : "null");
        
        // Lấy error message từ exception
        String errorMessage = "Đăng nhập OAuth2 thất bại. Vui lòng thử lại.";
        
        // Lấy message từ exception hoặc cause
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null && exception.getCause() != null) {
            exceptionMessage = exception.getCause().getMessage();
            log.info("Using cause message: {}", exceptionMessage);
        }
        
        // Kiểm tra nếu exception có message chứa OAuth2_ERROR prefix
        if (exceptionMessage != null && exceptionMessage.startsWith("OAuth2_ERROR:")) {
            // Lấy message gốc từ OAuth2_ERROR prefix
            String originalMessage = exceptionMessage.replace("OAuth2_ERROR:", "");
            log.info("Extracted OAuth2_ERROR message: {}", originalMessage);
            
            if (originalMessage.contains("vô hiệu hóa") || originalMessage.contains("bị khóa")) {
                errorMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
                log.info("Set error message to: {}", errorMessage);
            } else if (originalMessage.contains("chưa xác thực") || originalMessage.contains("not verified")) {
                errorMessage = "Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản.";
                log.info("Set error message to: {}", errorMessage);
            } else {
                errorMessage = originalMessage;
                log.info("Using OAuth2_ERROR message: {}", errorMessage);
            }
        } else if (exception.getCause() instanceof BadRequestException) {
            BadRequestException badRequestException = (BadRequestException) exception.getCause();
            
            // Lấy message từ BadRequestException
            String badRequestMessage = badRequestException.getMessage();
            log.info("BadRequestException message (from cause): {}", badRequestMessage);
            
            if (badRequestMessage != null) {
                if (badRequestMessage.contains("vô hiệu hóa") || badRequestMessage.contains("bị khóa")) {
                    errorMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
                    log.info("Set error message to: {}", errorMessage);
                } else if (badRequestMessage.contains("chưa xác thực") || badRequestMessage.contains("not verified")) {
                    errorMessage = "Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản.";
                    log.info("Set error message to: {}", errorMessage);
                } else {
                    errorMessage = badRequestMessage;
                    log.info("Using BadRequest message: {}", errorMessage);
                }
            }
        } else if (exception instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException) {
            org.springframework.security.oauth2.core.OAuth2AuthenticationException oauth2Exception = 
                (org.springframework.security.oauth2.core.OAuth2AuthenticationException) exception;
            
            // Lấy message từ OAuth2AuthenticationException
            String oauth2Message = oauth2Exception.getMessage();
            log.info("OAuth2AuthenticationException message: {}", oauth2Message);
            
            if (oauth2Message != null) {
                if (oauth2Message.contains("vô hiệu hóa") || oauth2Message.contains("bị khóa")) {
                    errorMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
                    log.info("Set error message to: {}", errorMessage);
                } else if (oauth2Message.contains("chưa xác thực") || oauth2Message.contains("not verified")) {
                    errorMessage = "Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản.";
                    log.info("Set error message to: {}", errorMessage);
                } else {
                    errorMessage = oauth2Message;
                    log.info("Using OAuth2 message: {}", errorMessage);
                }
            }
        } else if (exceptionMessage != null) {
            log.error("Processing exception message: {}", exceptionMessage);
            
            // Kiểm tra trực tiếp message gốc
            if (exceptionMessage.contains("vô hiệu hóa") || exceptionMessage.contains("bị khóa")) {
                errorMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
                log.info("Set error message to: {}", errorMessage);
            } else if (exceptionMessage.contains("chưa xác thực") || exceptionMessage.contains("not verified")) {
                errorMessage = "Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản.";
                log.info("Set error message to: {}", errorMessage);
            } else if (exceptionMessage.contains("OAuth2 login failed")) {
                // Lấy message gốc từ exception
                String originalMessage = exceptionMessage.replace("OAuth2 login failed: ", "");
                log.info("Extracted original message: {}", originalMessage);
                
                if (originalMessage.contains("vô hiệu hóa") || originalMessage.contains("bị khóa")) {
                    errorMessage = "Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
                    log.info("Set error message to: {}", errorMessage);
                } else if (originalMessage.contains("chưa xác thực") || originalMessage.contains("not verified")) {
                    errorMessage = "Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản.";
                    log.info("Set error message to: {}", errorMessage);
                } else {
                    errorMessage = originalMessage;
                    log.info("Using original message: {}", errorMessage);
                }
            } else {
                log.info("No specific pattern found, using default message: {}", errorMessage);
            }
        }
        
        log.info("Final error message: {}", errorMessage);
        
        // Encode error message để tránh vấn đề với URL
        String encodedError = java.net.URLEncoder.encode(errorMessage, "UTF-8");
        
        // Redirect về frontend login page với error message
        String redirectUrl = webDomain + "/login?error=" + encodedError;
        log.info("Redirecting to: {}", redirectUrl);
        log.error("=== OAuth2LoginFailureHandler END ===");
        response.sendRedirect(redirectUrl);
    }
}
