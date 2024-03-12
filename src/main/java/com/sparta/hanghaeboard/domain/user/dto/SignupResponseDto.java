package com.sparta.hanghaeboard.domain.user.dto;

import com.sparta.hanghaeboard.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {

    private String email;
    private String nickname;

    public SignupResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }


}
