package com.sparta.hanghaeboard.domain.comment.controller;

import com.sparta.hanghaeboard.domain.comment.dto.CommentRequestDto;
import com.sparta.hanghaeboard.domain.comment.dto.CommentResponseDto;
import com.sparta.hanghaeboard.domain.comment.service.CommentService;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API", description = "댓글과 관련된 API 정보를 담고 있습니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    // 선택한 강의의 댓글 등록
    @Operation(summary = "댓글 등록 기능", description = "댓글을 등록할 수 있는 API")
    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                                            @RequestBody CommentRequestDto commentRequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto commentResponseDto = commentService.createComment(postId, commentRequestDto, userDetails);
        return ResponseEntity.ok().body(ResponseDto.success("댓글 등록 완료", commentResponseDto));
    }

    // 선택한 강의의 댓글 수정
    @Operation(summary = "댓글 수정 기능", description = "댓글을 수정할 수 있는 API")
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId,
                                                            @PathVariable Long commentId,
                                                            @RequestBody CommentRequestDto commentRequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto commentResponseDto = commentService.updateComment(postId, commentId, commentRequestDto, userDetails);
        return ResponseEntity.ok().body(ResponseDto.success("댓글 수정 완료", commentResponseDto));
    }

    // 선택한 강의의 선택한 댓글 삭제
    @Operation(summary = "댓글 삭제 기능", description = "댓글을 삭제할 수 있는 API")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(postId, commentId, userDetails);
        return ResponseEntity.ok("댓글 삭제 완료");
    }




}
