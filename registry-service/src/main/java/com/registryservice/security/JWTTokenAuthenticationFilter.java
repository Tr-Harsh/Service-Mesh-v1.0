package com.registryservice.security;

import com.registryservice.model.CustomUserDetails;
import com.registryservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JWTTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JWTconfig jwtconfig;
    private JWTTokenProvider tokenProvider;
    private UserService userService;
    private String serviceUsername;

    public JWTTokenAuthenticationFilter(
            String serviceUsername,
            JWTconfig jwtConfig,
            JWTTokenProvider tokenProvider,
            UserService userService) {

        this.serviceUsername = serviceUsername;
        this.jwtconfig = jwtConfig;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(jwtconfig.getHeader());
        if (header == null || !header.startsWith(jwtconfig.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtconfig.getPrefix(), "");
        if (tokenProvider.validateToken(token)) {
            Claims claims = tokenProvider.getClaimsFromJwt(token);
            String username = claims.getSubject();
            UsernamePasswordAuthenticationToken auth = null;
            if (username.equals(serviceUsername)) {
                List<String> authorities = (List<String>) claims.get("authorities");
                auth = new UsernamePasswordAuthenticationToken(username, null,
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
            } else {
                auth = userService
                        .findByUsername(username)
                        .map(CustomUserDetails::new)
                        .map(userDetails -> {

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            authentication
                                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            return authentication;
                        }).orElse(null);

            }
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
