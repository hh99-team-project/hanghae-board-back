package com.sparta.hanghaeboard.domain.post.controller;



import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.*;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.*;
import com.sparta.hanghaeboard.domain.post.service.PostService;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 검증 user 필요
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreatePostResponseDto responseDto = postService.createPost(requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    // 검증 user 필요
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UpdatePostResponseDto responseDto = postService.updatePost(postId, requestDto, userDetails.getUser());
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

        // 댓글이랑 같이 반환하기 추가 필요.

        return ResponseEntity.ok().body(ResponseDto.success("조회 성공", responseDto));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostList(){
        List<GetPostResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok().body(ResponseDto.success("조회 성공", postList));
    }
}
