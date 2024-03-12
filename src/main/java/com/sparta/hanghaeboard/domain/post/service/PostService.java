package com.sparta.hanghaeboard.domain.post.service;

import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.*;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.*;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.repository.PostRepository;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public CreatePostResponseDto createPost(CreatePostRequestDto requestDto, User user) {
        Post post = requestDto.toEntity(user);
        Post savedPost = postRepository.save(post);
        return new CreatePostResponseDto(savedPost);
    }

    @Transactional
    public UpdatePostResponseDto updatePost(Long postId, UpdatePostRequestDto requestDto, User user) {
        // PostId를 통해 찾기 + 유저랑 같이 찾도록 변경
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );

        // 맞으면 로직 수행
        post.update(requestDto);
        return new UpdatePostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        // User랑 같이 찾도록 변경
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        postRepository.delete(post);
    }

    public GetPostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        return new GetPostResponseDto(post);
    }

    // 페이징 필요
    public List<GetPostResponseDto> getPostList() {
        return postRepository.findAll().stream().map(GetPostResponseDto::new).toList();
    }
}
