package com.sparta.hanghaeboard.global.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.hanghaeboard.domain.user.dto.LoginRequestDto;
import com.sparta.hanghaeboard.domain.user.entity.UserRoleEnum;
import com.sparta.hanghaeboard.global.user.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

//인증처리
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login"); // POST 사용
    }

    // 로그인 시도 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // 요청 본문에서 로그인 정보를 추출하여 LoginRequestDto 객체로 변환
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            // 로그인 정보를 이용하여 UsernamePasswordAuthenticationToken 생성하여 반환
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            // 예외 발생 시 로깅 후 RuntimeException 발생
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
//
    // 로그인 성공 처리
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 인증 결과에서 사용자 정보와 권한(Role) 추출
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId(); // userId 추출
        String nickname = (((UserDetailsImpl) authResult.getPrincipal()).getUser().getNickname());

        // JWT 토큰 생성
        String token = jwtUtil.createToken(email, role);
        // HTTP 응답 헤더에 JWT 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        // 로그인 성공 메시지를 로그에 출력
        log.info("사용자 '{}'의 로그인 성공", email);

        // 한글을 인코딩할 때 발생하는 문제를 해결하기 위한  UTF-8 인코딩을 설정 코드
        response.setCharacterEncoding("UTF-8");

        // void 일때 반환되는 값을 쓰고 싶을때 사용 (throws IOException, ServletException도 위에 기재해야한다)
        String responseBody = "로그인을 완료했습니다. " + "\n"
                + "사용자 아이디: " + userId + "\n"
                + "사용자 email: " + email + "\n"
                + "사용자 nickname: " + nickname + "\n"
                + "사용자 role: " + role;
        response.getWriter().write(responseBody);


    }

    // 로그인 실패 처리
    @SneakyThrows
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 인증 실패 시 401 Unauthorized 상태 코드 반환
        response.setCharacterEncoding("UTF-8");
        String responseDto = new ObjectMapper().writeValueAsString(Map.of("data", "BAD_REQUEST","message", "아이디 혹은 비밀번호를 확인해주세요.", "resultCode", HttpServletResponse.SC_BAD_REQUEST));
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(responseDto);
        response.getWriter().flush();
        response.getWriter().close();

        // 로그인 실패 메시지를 로그에 출력
        log.info("로그인 실패: {}", failed.getMessage());

//        // 포스트맨으로 "로그인을 실패했습니다" 메시지 보내기
//        response.getWriter().write("로그인을 실패했습니다");
//        response.getWriter().flush();
//        //utf8 인코딩을 하면 ??? 없어진다.
////        response.encoding?
    }
}
