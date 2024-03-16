package com.sparta.hanghaeboard.domain.post.controller;


import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.CreatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.UpdatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.CreatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostListResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.UpdatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.service.PostService;
import com.sparta.hanghaeboard.global.common.dto.ResponseDto;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "게시글 API", description = "게시글 CRUD")
@Slf4j(topic = "PostController 로그")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    @Autowired
    private final PostService postService;

    @Operation(summary = "게시글 등록", description = "게시글 등록: title, contents, category, file")
    @PostMapping(value = "/posts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                        @RequestPart(value = "createPostRequestDto") CreatePostRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        CreatePostResponseDto responseDto = postService.createPost(requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    @Operation(summary = "게시글 수정", description = "게시글 수정: title, contents, category, file")
    @PostMapping(value = "/posts/{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateImgPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                           @RequestPart(value = "updatePostRequestDto") UpdatePostRequestDto requestDto,
                                           @PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        UpdatePostResponseDto responseDto = postService.updatePost(postId, requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("수정 성공", responseDto));
    }

    @Operation(summary = "게시글 삭제", description = "유저 정보가 일치할 경우, 게시글 삭제 가능")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("삭제가 완료되었습니다.", null));
    }

    @Operation(summary = "게시글 상세 조회", description = "postId를 통한 게시글 상세 조회 - 게시글에 포함된 이미지, 댓글 포함")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        GetPostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok().body(ResponseDto.success("상세 조회 성공", responseDto));
    }

    @Operation(summary = "게시글 전체 조회", description = "전체 조회시, 글에 저장된 첫 번째 이미지 출력")
    @GetMapping("/posts")
    public ResponseEntity<?> getPostList() {
        List<GetPostListResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok().body(ResponseDto.success("전체 게시글 조회 성공", postList));
    }

    // 검색
    @GetMapping ("/posts/search")
    public ResponseEntity<?> searchPost (
            @RequestParam (value = "title", required = false) String title,
            @RequestParam (value = "contents", required = false) String contents,// 이게 왜 안되는지 체크
//            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "1")int num) {

        int pageNumber = Math.max(num - 1, 0); // 페이지 번호는 사용자 입력과 동일하게 설정
        int pageSize = 10; // 페이지당 표시할 데이터의 개수

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));



        Page<GetPostListResponseDto> postsPage;
        if (title == null || title.isEmpty()) {
            postsPage = postService.searchPost(pageable);
        } else {
            postsPage = postService.titleSearchPost(title, pageable);
        }

        // 현재 페이지 및 페이지 범위 계산
        int nowPage = postsPage.getPageable().getPageNumber() + 1; // 0부터 시작하므로 +1
        int startPage = Math.max(nowPage - 4, 1); // 현재 페이지 기준으로 시작 페이지 설정
        int endPage = Math.min(nowPage + 5, postsPage.getTotalPages()); // 현재 페이지 기준으로 끝 페이지 설정

        // 이전 페이지 및 다음 페이지의 존재 여부 계산
        boolean hasNext = postsPage.hasNext();
        boolean hasPrev = postsPage.hasPrevious();

        // 응답으로 반환할 데이터를 Map에 담음
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("resultCode", "SUCCESS"); // 결과 코드 설정
        responseBody.put("message", "검색 성공"); // 메시지 설정
        responseBody.put("data", postsPage.getContent()); // 현재 페이지의 포스트 리스트
        responseBody.put("nowPage", nowPage); // 현재 페이지 번호
        responseBody.put("startPage", startPage); // 시작 페이지 번호
        responseBody.put("endPage", endPage); // 끝 페이지 번호
        responseBody.put("hasNext", hasNext); // 다음 페이지 존재 여부
        responseBody.put("hasPrev", hasPrev); // 이전 페이지 존재 여부

        return ResponseEntity.ok().body(ResponseDto.success("검색 성공", responseBody));
    }

    @GetMapping("/posts/category/{category}")
    public ResponseEntity<?> getPostByCategoryList(@PathVariable String category) {
        List<GetPostListResponseDto> postList = postService.getPostByCategoryList(category);
        return ResponseEntity.ok().body(ResponseDto.success("전체 게시글 조회 성공", postList));
    }

    // hit
    @GetMapping("/posts/read/{id}")
    public String readHit (@PathVariable Long id) {
        postService.updateHit(id); // views++
        return "success";
    }

}
