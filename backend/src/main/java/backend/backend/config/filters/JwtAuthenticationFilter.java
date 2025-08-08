package backend.backend.config.filters;

import backend.backend.dao.UserDAO;
import backend.backend.entity.User;
import backend.backend.entity.UserRole;
import backend.backend.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j 
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;
    private final UserDAO userDAO;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                      HttpServletResponse response,
                                      FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        try {
            final String email = jwtUtil.extractUserEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
               
                
                User user = userDAO.findByEmailWithRoles(email).orElse(null);

                if (user == null) {
                
                } else if (jwtUtil.validateToken(token, user.getEmail())) {
                   
                    List<UserRole> userRoles = user.getUserRoles() != null ? user.getUserRoles() : Collections.emptyList();
                    

                    List<SimpleGrantedAuthority> authorities = userRoles.stream()
                            .map(userRole -> {
                                String roleName = userRole.getRole().getName().name();
                               
                                return new SimpleGrantedAuthority("ROLE_" + roleName);
                            })
                            .collect(Collectors.toList());

                    

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    authorities
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                   
                }
            }
        } catch (Exception e) {
           
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}