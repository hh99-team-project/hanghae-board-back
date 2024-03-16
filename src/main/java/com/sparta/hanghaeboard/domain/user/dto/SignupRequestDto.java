package com.sparta.hanghaeboard.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Email
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$",
            message = "이메일 형식에 맞지 않습니다.")
    private String email; // 이메일

    @NotBlank (message = "비밃번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 최소 8자에서 최대 15자까지, 소문자, 대문자, 숫자, 특수 문자(@, $, !, %, *, ?, &)를 포함해야 합니다.")
    private String password;  // 비밀번호

    @NotBlank (message = "nickname을 넣어주세요") // 로그에 뜬다
    private String nickname;

    private String adminToken = "";  // 권한토큰
}
