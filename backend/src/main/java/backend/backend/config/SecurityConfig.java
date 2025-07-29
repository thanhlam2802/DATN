package backend.backend.config;

import backend.backend.config.filters.OAuth2LoginSuccessHandler;
import backend.backend.service.OAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import backend.backend.config.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint((userInfo) -> {
                            userInfo.userService(oAuth2UserService);
                        })
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureUrl("/error")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}