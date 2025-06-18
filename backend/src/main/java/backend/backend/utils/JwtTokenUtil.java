package backend.backend.utils;



import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Lớp tiện ích để xử lý các hoạt động liên quan đến JSON Web Token (JWT)
 * như tạo, xác thực, và trích xuất thông tin từ token.
 */
@Component
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
     * Trích xuất username từ JWT token.
     *
     * @param token Chuỗi JWT.
     * @return Username chứa trong token.
     */
    public String extractUsername(String token) {
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
     * Kiểm tra xem token đã hết hạn hay chưa.
     *
     * @param token Chuỗi JWT.
     * @return true nếu token đã hết hạn, ngược lại false.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    /**
     * Tạo ra một JWT token mới cho người dùng.
     *
     * @param userDetails Chi tiết thông tin người dùng.
     * @return Chuỗi JWT được tạo ra.
     */
//    public String generateToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
//              
//                .signWith(secretKey)
//                .compact();
//    }
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