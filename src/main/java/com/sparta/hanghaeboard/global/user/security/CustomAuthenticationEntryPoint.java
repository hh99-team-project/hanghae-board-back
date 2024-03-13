package com.sparta.hanghaeboard.global.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());


        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ResponseDto.error(
                    ErrorCode.AUTHORITY_ACCESS.getKey(),
                    ErrorCode.AUTHORITY_ACCESS.getMessage(),
                    ErrorCode.AUTHORITY_ACCESS.getHttpStatus()));
            os.flush();
        }
    }
}
