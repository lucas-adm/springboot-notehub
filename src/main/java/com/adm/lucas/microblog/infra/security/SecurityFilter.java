package com.adm.lucas.microblog.infra.security;

import com.adm.lucas.microblog.application.service.SecurityService;
import com.adm.lucas.microblog.domain.model.User;
import com.adm.lucas.microblog.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService service;

    @Autowired
    private UserRepository repository;

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) {
            return null;
        }
        return header.replace("Bearer ", "");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getToken(request);
        if (accessToken != null) {
            try {
                UUID id = UUID.fromString(service.validateToken(accessToken));
                User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException | EntityNotFoundException ex) {
                try {
                    String email = service.validateToken(accessToken);
                    User user = repository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (EntityNotFoundException exception) {
                    throw new EntityNotFoundException();
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}