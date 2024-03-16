package com.sparta.hanghaeboard.domain.post.dto;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PostRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class CreatePostRequestDto {

        @Schema(description = "제목", example = "게시글 작성 예시입니다.")
        private String title;
        @Schema(description = "내용", example = "여기에 내용을 입력하면 됩니다!")
        private String contents;
        @Schema(description = "카테고리", example = "경제, 사회, 문화... (아직 카테고리화를 안해서, 아무값이나 들어갈 수 있습니다.)")
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

        @Schema(description = "수정할 제목", example = "게시글 수정 예시입니다.")
        private String title;
        @Schema(description = "수정할 내용", example = "여기에 수정할 내용을 입력하면 됩니다!")
        private String contents;
        @Schema(description = "수정할 카테고리", example = "경제, 사회, 문화, IT, 스포츠 ...")
        private String category;
        @Schema(description = "해당 게시글에 이미지가 있었던 경우, 그 imgId를 입력해주세요", example = "1")
        private Long imgId;

        public Post toEntity(User user) {
            return Post.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .category(this.category)
                    .user(user)
                    .build();
        }
    }
}
