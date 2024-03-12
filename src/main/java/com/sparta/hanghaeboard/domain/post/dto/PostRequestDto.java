package com.sparta.hanghaeboard.domain.post.dto;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

public class PostRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class CreatePostRequestDto {

        private String title;

        private String contents;

        private String category;

        public Post toEntity(User user) {
            return Post.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .category(this.category)
                    .user(user)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class UpdatePostRequestDto {

        private String title;

        private String contents;

        private String category;

        public Post toEntity() {
            return Post.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .category(this.category)
                    .build();
        }
    }
}
