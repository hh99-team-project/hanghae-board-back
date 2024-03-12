package com.sparta.hanghaeboard.domain.post.dto;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreatePostResponseDto {
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private LocalDateTime createdAt;

        public CreatePostResponseDto(Post post) {
            this.nickname = "닉네임";
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory();
            this.createdAt = post.getCreatedAt();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdatePostResponseDto {
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public UpdatePostResponseDto(Post post) {
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class GetPostResponseDto {
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public GetPostResponseDto(Post post) {
            this.nickname = "닉네임";
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }
    }
}
