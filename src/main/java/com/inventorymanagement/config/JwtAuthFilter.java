package com.inventorymanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that intercepts every HTTP request to validate
 * the Bearer token in the Authorization header.
 *
 * This filter extends OncePerRequestFilter to guarantee it executes exactly
 * once per request, even in complex filter chains. It performs the following steps:
 *
 * 1. Checks whether the request targets a public endpoint; if so, skips validation.
 * 2. Extracts the JWT from the "Authorization: Bearer <token>" header.
 * 3. Parses the username from the token and loads the corresponding UserDetails.
 * 4. Validates the token (signature, expiration, username match).
 * 5. If valid, sets the authentication in the SecurityContext so that downstream
 *    filters and controllers recognize the user as authenticated.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Public endpoints that should not trigger JWT validation.
     * These paths match the permitAll() rules defined in SecurityConfig.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AntPathMatcher pathMatcher;

    /**
     * Constructor injection for required dependencies.
     *
     * @param jwtTokenProvider         utility for parsing and validating JWT tokens
     * @param customUserDetailsService service for loading user details from the database
     */
    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider,
                         CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.pathMatcher = new AntPathMatcher();
    }

    /**
     * Determines whether this filter should be skipped for the given request.
     * Public endpoints (authentication, Swagger, health check) do not require
     * JWT validation, so we skip the filter to avoid unnecessary processing.
     *
     * @param request the incoming HTTP request
     * @return true if the request targets a public endpoint, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Core filter logic: extracts the JWT from the request, validates it,
     * and sets the SecurityContext if the token is valid.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Step 1: Extract the JWT from the Authorization header
            String jwt = extractTokenFromRequest(request);

            if (jwt != null) {
                // Step 2: Parse the username from the token
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // Step 3: Only proceed if the user is not already authenticated
                // (avoids redundant database lookups on already-authenticated requests)
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Step 4: Load the full user details from the database
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // Step 5: Validate the token against the loaded user details
                    if (jwtTokenProvider.validateToken(jwt, userDetails)) {

                        // Step 6: Create an authentication token and set it in the SecurityContext
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.debug("Authenticated user: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but do not halt the filter chain; the request will
            // proceed without authentication and be rejected by Spring Security
            // if the endpoint requires it.
            logger.error("Could not set user authentication in security context: {}", e.getMessage());
        }

        // Continue the filter chain regardless of authentication outcome
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * The expected format is "Bearer <token>".
     *
     * @param request the incoming HTTP request
     * @return the JWT string if present and properly formatted, null otherwise
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
