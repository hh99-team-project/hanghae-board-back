package com.sparta.hanghaeboard.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.hanghaeboard.domain.comment.dto.CommentResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostImageResponseDto.DetailPostImageResponseDto;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.entity.PostImage;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreatePostResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private int hit;
        private List<String> imageName;
        private List<String> imageUrlList;
        private List<DetailPostImageResponseDto> postImageList = new ArrayList<>();
        private LocalDateTime createdAt;

        public CreatePostResponseDto(Post post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
//            this.postImageList = post.getPostImageList().stream().map(DetailPostImageResponseDto::new).toList();
        }

        public CreatePostResponseDto(Post post, List<String> urlList, List<String> nameList) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.imageUrlList = urlList;
            this.imageName = nameList;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdatePostResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private int hit;
        private List<String> imageName;
        private List<String> imageUrlList;
        private List<DetailPostImageResponseDto> postImageList = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public UpdatePostResponseDto(Post post, List<String> urlList, List<String> nameList) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.imageUrlList = urlList;
            this.imageName = nameList;
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }
        public UpdatePostResponseDto(Post post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.postImageList = post.getPostImageList().stream().map(DetailPostImageResponseDto::new).toList();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class GetPostResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private int hit;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private List<CommentResponseDto> commentList = new ArrayList<>();
        private List<DetailPostImageResponseDto> postImageList = new ArrayList<>();

        public GetPostResponseDto(Post post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
//            this.commentList = post.getCommentList().stream().map(CommentResponseDto::new).toList();
            this.postImageList = post.getPostImageList().stream().map(DetailPostImageResponseDto::new).toList();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class GetPostListResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String contents;
        private String category;
        private int hit;
        private DetailPostImageResponseDto postImage;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public GetPostListResponseDto(Post post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getCategory().getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            if (!post.getPostImageList().isEmpty()) {
                this.postImage = post.getPostImageList()
                        .stream()
                        .findFirst()
                        .map(DetailPostImageResponseDto::new)
                        .orElse(null);
            }
        }
    }
}
