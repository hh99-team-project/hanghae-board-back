package com.sparta.hanghaeboard.domain.comment.dto;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.antlr.v4.runtime.misc.NotNull;

@AllArgsConstructor
@Getter
@Builder
public class CommentRequestDto {
    @Schema(description = "댓글 내용", example = "댓글 내용입니다")
    @NotNull
    private String comment; // 댓글 내용

    public Comment toEntity() {
        return Comment.builder()
                .comment(this.comment)
                .build();
    }

}
