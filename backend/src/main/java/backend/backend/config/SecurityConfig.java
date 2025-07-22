package backend.backend.config;

import backend.backend.config.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// KHÔNG CẦN CÁC IMPORT NÀY NỮA
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**", "/graphql/**", "/graphiql", "/vendor/**")
                        .permitAll() // Cho phép các endpoint này
                        .anyRequest().permitAll() // <-- TẠM THỜI CHO PHÉP TẤT CẢ CÁC REQUEST KHÁC CHO MỤC ĐÍCH PHÁT TRIỂN
                );
        // --- ĐÂY LÀ DÒNG BẠN CẦN COMMENT HOẶC XÓA TẠM THỜI ---
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Đã thay đổi kiểu trả về và sửa cấu hình allowedOrigins
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // THAY ĐỔI TẠI ĐÂY: Liệt kê các origin CỤ THỂ thay vì "*"
        config.addAllowedOrigin("https://poly-java-6-fb151.web.app");
        config.addAllowedOrigin("https://www.travela.io.vn");
        config.addAllowedOrigin("http://localhost:5173");
        // Nếu bạn muốn cho phép cả các subdomain hoặc các pattern, bạn có thể dùng addAllowedOriginPattern()
        // Ví dụ: config.addAllowedOriginPattern("http://*.yourdomain.com");

        config.addAllowedHeader("*");    // Cho phép tất cả các header
        config.addAllowedMethod("*");    // Cho phép tất cả các phương thức HTTP (GET, POST, PUT, DELETE, OPTIONS)
        config.setAllowCredentials(true); // Cho phép gửi cookie/authentication headers (bây giờ hợp lệ vì đã liệt kê origin cụ thể)

        source.registerCorsConfiguration("/**", config); // Áp dụng cấu hình này cho tất cả các URL
        return source;
    }

    // XÓA BỎ PHƯƠNG THỨC NÀY NẾU NÓ ĐANG CÓ TRONG FILE CỦA BẠN
    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                     .allowedOrigins("https://poly-java-6-fb151.web.app", "https://www.travela.io.vn", "http://localhost:5173")
    //                     .allowedMethods("*")
    //                     .allowedHeaders("*");
    //         }
    //     };
    // }
}