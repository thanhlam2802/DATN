package backend.backend.config.filters;

import backend.backend.dao.UserDAO;
import backend.backend.entity.User;
import backend.backend.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequestMapping
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;
    private final UserDAO userDAO;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String email = jwtUtil.extractUserEmail(token);
                Claims claims = jwtUtil.extractAllClaims(token);
                User user = userDAO.findByEmailWithRoles(email).orElse(null);
                Map<String, Object> credentials = new HashMap<>();
                credentials.put("email", email);
                credentials.put("userId", claims.get("userId"));
                
                List<SimpleGrantedAuthority> authorities = List.of();
                if (user != null && user.getUserRoles() != null) {
                    authorities = user.getUserRoles().stream()
                            .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName().name()))
                            .collect(Collectors.toList());
                }
                
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            email,
                            credentials, 
                            authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

}
