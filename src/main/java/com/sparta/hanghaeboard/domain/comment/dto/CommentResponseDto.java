package com.sparta.hanghaeboard.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class CommentResponseDto {
    private String nickname; // 댓글내용
    private String comment; // 댓글내용

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt; // 등록일

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt; // 수정일

    public CommentResponseDto(Comment comment) {
        this.nickname = comment.getUser.getNickname();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
