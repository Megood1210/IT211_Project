package com.rikkeibank.security.jwt;

import com.rikkeibank.repository.TokenBlacklistRepository;
import com.rikkeibank.security.principal.CustomUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        if (tokenBlacklistRepository.existsByAccessToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("Token revoked");

            return;
        }

        String username = jwtProvider.extractUsername(jwt);

        System.out.println("JWT USERNAME = " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);

            System.out.println("AUTHORITIES = " + userDetails.getAuthorities());

            if (jwtProvider.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("AUTH SET = " + auth.getAuthorities());
            }
        }

        filterChain.doFilter(request, response);
    }
}