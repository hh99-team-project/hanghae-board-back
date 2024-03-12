package com.sparta.hanghaeboard.domain.post.controller;

import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.*;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.*;
import com.sparta.hanghaeboard.domain.post.service.PostService;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 검증 user 필요
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto requestDto, User user) {
        CreatePostResponseDto responseDto = postService.createPost(requestDto, user);
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    // 검증 user 필요
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequestDto requestDto, User user) {
        UpdatePostResponseDto responseDto = postService.updatePost(postId, requestDto, user);
        return ResponseEntity.ok().body(ResponseDto.success("수정 성공", responseDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, User user) {
        postService.deletePost(postId, user);
        return ResponseEntity.ok().body(ResponseDto.success("삭제가 완료되었습니다.", null));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        GetPostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok().body(ResponseDto.success("조회 성공", responseDto));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostList(){
        List<GetPostResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok().body(ResponseDto.success("조회 성공", postList));
    }
}
