package com.sparta.hanghaeboard.domain.user.dto;

import com.sparta.hanghaeboard.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String message = "회원가입을 성공했습니다";

    public SignupResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }


}
