package com.sparta.hanghaeboard.domain.post.service;

import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.CreatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.UpdatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.CreatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.UpdatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.repository.PostRepository;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
    public List<GetPostResponseDto> getPostList() {
        return postRepository.findAll().stream().map(GetPostResponseDto::new).toList();
    }
}
