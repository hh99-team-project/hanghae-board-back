package com.sparta.hanghaeboard.domain.user.controller;

import com.sparta.hanghaeboard.domain.user.dto.CheckResponseDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupResponseDto;
import com.sparta.hanghaeboard.domain.user.service.UserService;
import com.sparta.hanghaeboard.global.user.blacklist.TokenBlacklist;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping ("/api")
public class UserController {

    private final UserService userService;
    @Autowired
    private TokenBlacklist tokenBlacklist;

    // 회원 가입
    @PostMapping("/user/signup")
    // ResponseEntity : HTTP 응답을 나타내는 클래스입니다. HTTP 응답의 상태 코드, 헤더 및 본문(body)을 포함합니다.
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body("회원가입 요청이 잘못되었습니다.");
        }
        userService.signup(signupRequestDto);
        SignupResponseDto signupResponseDto = userService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(signupResponseDto);
    }

    // 회원 정보 조회
    @GetMapping ("/auth")
    public ResponseEntity<Object> checkInfo (@AuthenticationPrincipal UserDetailsImpl userDetails) {

        CheckResponseDto checkResponseDto = userService.checkInfo(userDetails);

        if (checkResponseDto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(checkResponseDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원정보가 틀렸습니다.");

    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout (HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // 로그아웃된 사용자의 토큰을 블랙리스트에 추가합니다.
        String authToken = request.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            tokenBlacklist.addToBlacklist(token);
            return ResponseEntity.ok("토큰을 블랙리스트에 추가하였습니다.");
        }

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/checkToken")
    public ResponseEntity<String> checkToken(HttpServletRequest request) {
        // 요청 헤더에서 토큰 추출
        String authToken = request.getHeader("Authorization");
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("유효한 토큰이 아닙니다.");
        }

        // 토큰에서 실제 토큰 값 추출
        String token = authToken.substring(7);

        // 블랙리스트에서 토큰 유효성 확인
        if (tokenBlacklist.isBlacklisted(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("블랙리스트에 포함된 토큰입니다.");
        } else {
            return ResponseEntity.ok("토큰이 유효합니다.");
        }
    }
}
