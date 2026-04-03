package com.wordcrush.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordcrush.server.common.api.ApiResponse;
import com.wordcrush.server.common.api.LegacyApiResponse;
import com.wordcrush.server.common.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> LEGACY_PROTECTED_ENDPOINTS = List.of(
            "/api/addGameRecord",
            "/api/deleteGameRecord",
            "/api/getAllGameRecord"
    );

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public TokenAuthenticationFilter(TokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            writeFailureResponse(request, response, 401, "token must not be blank");
            return;
        }

        try {
            TokenSession session = tokenService.requireValidSession(token);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(session, token, List.of()));
            SecurityContextHolder.setContext(context);
            filterChain.doFilter(request, response);
        } catch (BusinessException exception) {
            writeFailureResponse(request, response, exception.getCode(), exception.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/")) {
            return false;
        }
        if ("/api/user/login".equals(uri) || "/api/user/register".equals(uri) || "/api/user/checkToken".equals(uri)) {
            return false;
        }
        if ("/api/getTopNRecord".equals(uri)) {
            return false;
        }
        return !("GET".equalsIgnoreCase(request.getMethod()) && uri.startsWith("/api/user/avatar/"));
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }

        String headerToken = request.getHeader("token");
        if (StringUtils.hasText(headerToken)) {
            return headerToken.trim();
        }

        String parameterToken = request.getParameter("token");
        if (StringUtils.hasText(parameterToken)) {
            return parameterToken.trim();
        }
        return null;
    }

    private void writeFailureResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            int code,
            String message
    ) throws IOException {
        response.setStatus(resolveHttpStatus(code));
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (LEGACY_PROTECTED_ENDPOINTS.contains(request.getRequestURI())) {
            objectMapper.writeValue(response.getWriter(), LegacyApiResponse.fail(message));
            return;
        }
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(code, message));
    }

    private int resolveHttpStatus(int code) {
        return switch (code) {
            case 401 -> HttpServletResponse.SC_UNAUTHORIZED;
            case 403 -> HttpServletResponse.SC_FORBIDDEN;
            case 404 -> HttpServletResponse.SC_NOT_FOUND;
            case 409 -> HttpServletResponse.SC_CONFLICT;
            default -> HttpServletResponse.SC_BAD_REQUEST;
        };
    }
}
