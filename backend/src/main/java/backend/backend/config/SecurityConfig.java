package backend.backend.config;

import backend.backend.config.filters.OAuth2LoginSuccessHandler;
import backend.backend.config.filters.OAuth2LoginFailureHandler;
import backend.backend.service.OAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import backend.backend.config.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/hotels/**").permitAll()
                        .requestMatchers("/api/v1/provinces/**").permitAll()
                        .requestMatchers("/api/v1/tours/**").permitAll()
                        .requestMatchers("/api/v1/tour-schedules/**").permitAll()
                        .requestMatchers("/api/v1/bookings/tours/**").permitAll()
                        .requestMatchers("/api/v1/flights/**").permitAll()
                        .requestMatchers("/api/flights/**").permitAll()
                        .requestMatchers("/api/v1/vouchers/**").permitAll()
                        .requestMatchers("/api/vouchers/**").permitAll()
//                        --------------- LHN them doan nay
                        .requestMatchers("/api/v1/orders/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/orders/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT,  "/api/v1/orders/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE,"/api/v1/orders/**").authenticated()
//                        -----------------------------------------------------
                        .requestMatchers("/api/v1/cart/**").permitAll()
                        .requestMatchers("/api/v1/reviews/**").permitAll()
                        .requestMatchers("/api/v1/notifications/**").permitAll()
                        .requestMatchers("/api/v1/hotel-notifications/**").permitAll()
                        .requestMatchers("/api/v1/payment-notifications/**").permitAll()
                        .requestMatchers("/api/v1/review-notifications/**").permitAll()
                        .requestMatchers("/api/v1/search/**").permitAll()
                        .requestMatchers("/api/v1/payment/**").permitAll()
                        .requestMatchers("/api/v1/payments/**").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/v1/account-lookup/**").permitAll()
                        .requestMatchers("/api/v1/accounts/**").permitAll()
                        .requestMatchers("/api/v1/refunds/**").permitAll()
                        .requestMatchers("/api/v1/refunds/by-transaction/**").permitAll()
                        .requestMatchers("/api/v1/email/**").permitAll()
                        .requestMatchers("/api/v1/otp/**").permitAll()
                        .requestMatchers("/api/v1/upload/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/v1/tags/**").permitAll()
                        .requestMatchers("/api/tags/**").permitAll()
                        .requestMatchers("/api/v1/airports/**").permitAll()
                        .requestMatchers("/api/v1/airlines/**").permitAll()
                        .requestMatchers("/api/v1/cities/**").permitAll()
                        .requestMatchers("/api/v1/countries/**").permitAll()
                        .requestMatchers("/api/v1/amenities/**").permitAll()
                        .requestMatchers("/api/v1/customers/**").permitAll()
                        .requestMatchers("/api/v1/account/**").permitAll()
                        .requestMatchers("/api/v1/websocket/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/suppliers/apply").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/v1/suppliers/applications/**").hasRole("SUPER_ADMIN")
                        .anyRequest().permitAll()
                        
                        
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint((userInfo) -> {
                            userInfo.userService(oAuth2UserService);
                        })
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://poly-java-6-fb151.web.app", "https://www.travela.io.vn", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}