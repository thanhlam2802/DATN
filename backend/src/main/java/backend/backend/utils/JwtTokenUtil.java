package backend.backend.utils;


import backend.backend.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Lớp tiện ích để xử lý các hoạt động liên quan đến JSON Web Token (JWT)
 * như tạo, xác thực, và trích xuất thông tin từ token.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    /**
     * Hàm này được gọi sau khi các dependency được inject.
     * Nó dùng để khởi tạo secretKey từ chuỗi bí mật.
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * Trích xuất useremail từ JWT token.
     *
     * @param token Chuỗi JWT.
     * @return user email chứa trong token.
     */
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất ngày hết hạn từ JWT token.
     *
     * @param token Chuỗi JWT.
     * @return Ngày hết hạn của token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Hàm chung để trích xuất một claim (thông tin) cụ thể từ token.
     *
     * @param token          Chuỗi JWT.
     * @param claimsResolver Một hàm để chỉ định claim nào cần lấy.
     * @return Giá trị của claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    /**
     * Parse token và lấy ra toàn bộ các claims.
     * Đây là hàm cốt lõi, chỉ thực hiện parse một lần duy nhất để tăng hiệu suất.
     *
     * @param token Chuỗi JWT.
     * @return Đối tượng Claims chứa toàn bộ thông tin.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token đã hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token không được hỗ trợ: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("JWT token không đúng định dạng: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Lỗi chữ ký JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Chuỗi JWT claims rỗng: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Tạo ra một JWT token mới cho người dùng.
     *
     * @param user Chi tiết thông tin người dùng.
     * @return Chuỗi JWT được tạo ra.
     */

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
//
//    /**
//     * Xác thực token có hợp lệ với thông tin người dùng hay không.
//     *
//     * @param token       Chuỗi JWT cần xác thực.
//     * @param userDetails Chi tiết thông tin người dùng để so sánh.
//     * @return true nếu token hợp lệ, ngược lại false.
//     */
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//
//        return (username != null && username.equals(userDetails.getUsername()));
//    }
}