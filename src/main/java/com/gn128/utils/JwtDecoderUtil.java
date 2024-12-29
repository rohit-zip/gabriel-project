package com.gn128.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gn128.constants.ServiceConstants;
import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.payloads.response.JwtErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.utils
 * Created_on - November 10 - 2024
 * Created_at - 12:56
 */

@Component
@RequiredArgsConstructor
public class JwtDecoderUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtDecoderUtil.class);

    private final JwtDecoder jwtDecoder;

    public Collection<? extends GrantedAuthority> extractAuthorities(String jwtToken) {
        try {
            Jwt jwt = jwtDecoder.decode(jwtToken);
            List<String> authorities = jwt.getClaimAsStringList(ServiceConstants.AUTHORITIES);
            return authorities
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        } catch (Exception exception) {
            logger.error("Exception Occurred while extracting Authorities with default message as : {}", exception.getMessage());
            throw new AuthenticationException(
                    "Exception Occurred while extracting the authorities with default message as : " + exception.getMessage()
            );
        }
    }

    public String extractUserId(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            logger.error("Exception Occurred while extracting User Id from token with default message as : {}", e.getMessage());
            throw new AuthenticationException(
                    "Exception Occurred while extracting User Id from token with default message as : " + e.getMessage()
            );
        }
    }

    public String extractUserIp(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString(ServiceConstants.USER_IP);
        } catch (Exception e) {
            logger.error("Exception Occurred while extracting Remote Address from token with default message as : {}", e.getMessage());
            throw new AuthenticationException(
                    "Exception Occurred while extracting User IP from token with default message as : " + e.getMessage()
            );
        }
    }

    @SneakyThrows
    public void validateJwtToken(String jwtToken, HttpServletResponse response) {
        try {
            Jwt jwt = jwtDecoder.decode(jwtToken);
        } catch (BadJwtException exception) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            OutputStream output = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(output, new JwtErrorResponse(exception.getMessage()));
            output.flush();
            return;
        } catch (Exception e) {
            throw new AuthenticationException(ServiceConstants.INTERNAL_ERROR_MESSAGE);
        }
    }
}
