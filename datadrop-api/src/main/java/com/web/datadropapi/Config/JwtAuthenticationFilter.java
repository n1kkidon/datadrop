package com.web.datadropapi.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.web.datadropapi.Handler.ErrorModel;
import com.web.datadropapi.Handler.ErrorResponse;
import com.web.datadropapi.Handler.Exception.UserNotAuthenticatedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private static final List<String> LOCAL_APIS = Arrays.asList("/guest/", "/file/download/");
    private static final List<String> ADMIN_ONLY_APIS = Arrays.asList("/admin/");
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authenticationHeader = request.getHeader("Authorization");

        if(authenticationHeader == null || !authenticationHeader.startsWith("Bearer") || authenticationHeader.length() <= 7) {
            if(LOCAL_APIS.stream().noneMatch(x -> request.getServletPath().startsWith(x))){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                setCustomResponse(response, "Unauthorized", request.getServletPath());
                return;
            }
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authenticationHeader.substring(7);

        if(!jwtService.isTokenValid(jwtToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setCustomResponse(response, "Invalid token", request.getServletPath());
            return;
        }

        var authorities = (ArrayList<LinkedHashMap<String, String>>)jwtService.extractClaim(jwtToken, claims -> claims.get("roles"));
        var isAdmin = authorities.stream().anyMatch(x -> x.get("authority").equals("ROLE_ADMIN"));

        if(ADMIN_ONLY_APIS.stream().anyMatch(x -> request.getServletPath().startsWith(x)) && !isAdmin){
           response.setStatus(HttpServletResponse.SC_FORBIDDEN);
           setCustomResponse(response, "Insufficient privileges", request.getServletPath());
           return;
        }

        String username = jwtService.extractUsername(jwtToken);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private void setCustomResponse(@NonNull HttpServletResponse response, String message, String servletPath) throws IOException {
        response.setStatus(response.getStatus());
        response.setHeader("Content-Type", "application/json");
        ErrorResponse errorModel = new ErrorResponse();
        errorModel.setTimestamp(String.valueOf(new Date()));
        errorModel.setStatus(response.getStatus());
        errorModel.setError(message);
        errorModel.setPath(servletPath);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorModel));
    }
}
