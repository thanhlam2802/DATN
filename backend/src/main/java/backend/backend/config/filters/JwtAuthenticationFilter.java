package backend.backend.config.filters;

import backend.backend.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;

    // Định nghĩa các đường dẫn công khai không yêu cầu xác thực JWT
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/upload", // Endpoint upload ảnh
            "/api/auth/**", // Các endpoint xác thực (đăng nhập, đăng ký) nếu có
            "/graphql" // Endpoint GraphQL (nếu bạn muốn cho phép truy cập public queries/mutations)
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Bỏ qua các OPTIONS request (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return; // Rất quan trọng để thoát sớm
        }

        // 2. Bỏ qua các đường dẫn công khai
        String requestUri = request.getRequestURI();
        // Kiểm tra xem requestUri có bắt đầu bằng bất kỳ PUBLIC_URL nào không
        boolean isPublicUrl = PUBLIC_URLS.stream().anyMatch(requestUri::startsWith);

        if (isPublicUrl) {
            filterChain.doFilter(request, response);
            return; // Rất quan trọng để thoát sớm
        }

        // 3. Xử lý JWT cho các request còn lại (yêu cầu xác thực)
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String email = jwtUtil.extractUserEmail(token);

                // Kiểm tra nếu người dùng đã được xác thực rồi thì không cần xác thực lại
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Trong trường hợp này, bạn có thể cần load UserDetails từ UserDetailsService
                    // và tạo UsernamePasswordAuthenticationToken với UserDetails và GrantedAuthorities
                    // Để đơn giản, tôi sẽ giữ nguyên cấu trúc của bạn, nhưng lưu ý cho tương lai
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    email, // Principal
                                    null,  // Credentials (không cần thiết sau khi xác thực)
                                    null   // Authorities (cần thiết cho phân quyền, bạn nên thêm vào đây)
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (Exception e) {
                // Log lỗi chi tiết hơn nếu cần
                logger.warn("JWT validation failed: {}", e.getCause()); // Cần import logger nếu chưa
                SecurityContextHolder.clearContext();
                // Tùy chọn: trả về lỗi 401 Unauthorized nếu token không hợp lệ
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
