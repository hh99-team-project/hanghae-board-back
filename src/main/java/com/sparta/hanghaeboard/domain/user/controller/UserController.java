package com.sparta.hanghaeboard.domain.user.controller;

import com.sparta.hanghaeboard.domain.user.dto.CheckResponseDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupResponseDto;
import com.sparta.hanghaeboard.domain.user.service.UserService;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//        userService.signup(signupRequestDto);
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
    public String logout (HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "로그아웃 되었습니다.";
    }
}
