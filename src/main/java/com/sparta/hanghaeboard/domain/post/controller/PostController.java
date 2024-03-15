package com.sparta.hanghaeboard.domain.post.controller;


import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.CreatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.UpdatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.CreatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostListResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.UpdatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.service.PostService;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "PostController 로그")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/posts", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                        @RequestPart(value = "createPostRequestDto") CreatePostRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        CreatePostResponseDto responseDto = postService.createPost(requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    @PostMapping(value = "/posts/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateImgPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                           @RequestPart(value = "updatePostRequestDto") UpdatePostRequestDto requestDto,
                                           @PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        UpdatePostResponseDto responseDto = postService.updatePost(postId, requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("수정 성공", responseDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("삭제가 완료되었습니다.", null));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        GetPostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok().body(ResponseDto.success("상세 조회 성공", responseDto));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostList() {
        List<GetPostListResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok().body(ResponseDto.success("전체 게시글 조회 성공", postList));
    }
}
