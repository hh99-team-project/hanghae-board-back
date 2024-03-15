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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    @Autowired
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreatePostResponseDto responseDto = postService.createPost(requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

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

        return ResponseEntity.ok().body(ResponseDto.success("상세 조회 성공", responseDto));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostList(){
        List<GetPostListResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok().body(ResponseDto.success("전체 게시글 조회 성공", postList));
    }

    // 페이징 처리 + 검색 기능 : 은미
//    @GetMapping ("/posts/search")
//    public String searchPost (Model model,
//                              @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
//                              String title) { // model : 데이터를 받아서 우리가 보는 페이지로 넘겨줄때 사용
//
//        Page<Post> list = null;
//
//        if (title == null) {
//            list = postService.searchPost(pageable);
//        } else {
//            list = postService.rightSearchPost(title, pageable);
//
//        }
//
//        int nowPage = list.getPageable().getPageNumber() +1;
//        int startPage = Math.max(nowPage -4, 1);
//        int endPage = Math.min(nowPage +5, list.getTotalPages());
//
//        model.addAttribute("search", list);// search라는 이름으로 보낸다, 뒤에 있는 것을
//        model.addAttribute("nowPage",nowPage);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        return "searchPost";
//    }


//    // 성공함. 그러나 더 build up 해보기
//    @GetMapping("/posts/search")
//    public ResponseEntity<?> searchPost(
////            @RequestParam(defaultValue = "0") int page,
////            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String title) {
//
//        int page = 0;
//        int size = 10;
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
//        Page<Post> postsPage;
//
//        if (title == null || title.isEmpty()) {
//            postsPage = postService.searchPost(pageable);
//        } else {
//            postsPage = postService.rightSearchPost(title, pageable);
//        }
//
//        return ResponseEntity.ok(postsPage);
//    }

    @GetMapping ("/posts/search")
    public ResponseEntity<?> searchPost (
            @RequestParam (value = "title", required = false) String title,
            @RequestParam (value = "contents", required = false) String contents,// 이게 왜 안되는지 체크
//            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "1")int num) {

        int pageNumber = Math.max(num - 1, 0); // 페이지 번호는 사용자 입력과 동일하게 설정
        int pageSize = 10; // 페이지당 표시할 데이터의 개수

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));



        Page<Post> postsPage;
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


}
