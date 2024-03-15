package com.sparta.hanghaeboard.domain.post.dto;

import com.sparta.hanghaeboard.domain.post.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostImageResponseDto {

    @NoArgsConstructor
    @Getter
    public static class DetailPostImageResponseDto {
        private Long id;
        private String imageName;
        private String url;

        public DetailPostImageResponseDto(PostImage postImage) {
            this.id = postImage.getId();
            this.url = postImage.getUrl();
            this.imageName = postImage.getImageName();
        }
    }
}
