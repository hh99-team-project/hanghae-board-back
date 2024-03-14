package com.sparta.hanghaeboard.domain.post.service;

import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.*;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.*;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.repository.PostRepository;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    @Autowired
    private final PostRepository postRepository;

    public CreatePostResponseDto createPost(CreatePostRequestDto requestDto, User user) {
        Post post = requestDto.toEntity(user);
        Post savedPost = postRepository.save(post);
        return new CreatePostResponseDto(savedPost);
    }

    @Transactional
    public UpdatePostResponseDto updatePost(Long postId, UpdatePostRequestDto requestDto, User user) {
        Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        post.update(requestDto);
        return new UpdatePostResponseDto(post);
    }

    public void deletePost(Long postId, User user) {
        Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        postRepository.delete(post);
    }

    // 성능 향상을 위해, readOnly 설정
    @Transactional(readOnly = true)
    public GetPostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        return new GetPostResponseDto(post);
    }

    @Transactional(readOnly = true)
    // 페이징 필요
    public List<GetPostListResponseDto> getPostList() {
        return postRepository.findAll().stream().map(GetPostListResponseDto::new).toList();
    }

    // 페이징 처리 + 검색 기능 : 은미
//    public Page<Post> searchPost(Pageable pageable) {
//
//        return postRepository.findAll(pageable);
//    }
//
//    public Page<Post> rightSearchPost(String title, Pageable pageable) {
//        return postRepository.findByTitleContaining(title,pageable);
//    }


     //buildup 해보기
    public Page<Post> searchPost(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> titleSearchPost(String title, Pageable pageable) {
        return postRepository.findByTitleContaining(title, pageable);
    }
}
