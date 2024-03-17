package com.sparta.hanghaeboard.domain.comment.dto;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentRequestDto {
    @Schema(description = "댓글 내용", example = "댓글 내용입니다")
    @NotNull
    private String comment; // 댓글 내용

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .user(user)
                .post(post)
                .comment(this.comment)
                .build();
    }

}
