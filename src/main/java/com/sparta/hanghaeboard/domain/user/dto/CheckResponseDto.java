package com.sparta.hanghaeboard.domain.user.dto;

import com.sparta.hanghaeboard.domain.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class CheckResponseDto {

    private Long id;
    private String nickname;

    private UserRoleEnum role;


    public CheckResponseDto(Long id, String nickname, UserRoleEnum role) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }
}
