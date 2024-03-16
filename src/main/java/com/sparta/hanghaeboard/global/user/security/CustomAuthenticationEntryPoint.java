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
// AuthenticationEntryPoint: Security에서 인증되지 않은 요청에 대한 처리를 담당
// 현재, 모르는 접근에 대해 AUTHORITY_ACCESS 에러 코드 반환 (접근 권한 없음)
//
// AccessDeniedHandler: Security 의 리소스에 대한 접근 권한이 없는 요청에 대한 핸들링
// ex) @Secured -> 권한에 따른 메서드
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


