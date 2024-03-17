package com.sparta.hanghaeboard.domain.post.service;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.comment.repository.CommentRepository;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.*;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.*;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.entity.PostCategory;
import com.sparta.hanghaeboard.domain.post.entity.PostImage;
import com.sparta.hanghaeboard.domain.post.repository.PostImageRepository;
import com.sparta.hanghaeboard.domain.post.repository.PostRepository;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.aws.service.S3UploadService;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    @Autowired
    private final PostRepository postRepository;
    private final S3UploadService s3UploadService;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreatePostResponseDto createPost(CreatePostRequestDto requestDto, MultipartFile[] multipartFileList, User user) throws IOException {

        if (multipartFileList == null) {
            Post post = requestDto.toEntity(user);
            Post savedPost = postRepository.save(post);
            return new CreatePostResponseDto(savedPost);
        }

        // Front responseDto 작성용
        List<String> createImageUrlList = new ArrayList<>();
        List<String> createImageNameList = new ArrayList<>();

        Post post = requestDto.toEntity(user);

        saveImgToS3(multipartFileList, post, createImageUrlList, createImageNameList);

        Post savedPost = postRepository.save(post);
        return new CreatePostResponseDto(savedPost, createImageUrlList, createImageNameList);
    }

    @Transactional
    public UpdatePostResponseDto updatePost(Long postId, UpdatePostRequestDto requestDto, MultipartFile[] multipartFileList, User user) throws IOException {

        List<String> updateImageUrlList = new ArrayList<>();
        List<String> updateImageNameList = new ArrayList<>();
        if (requestDto.getImgId() == null && multipartFileList == null) {
            Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                    new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            return new UpdatePostResponseDto(post);
        }

        // 파일만 넘어온 경우 (이미지를 등록하지 않았다가, 등록하는 경우)
        if (requestDto.getImgId() == null){
            Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                    new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            saveImgToS3(multipartFileList, post, updateImageUrlList, updateImageNameList);
            return new UpdatePostResponseDto(post, updateImageUrlList, updateImageNameList);
        }

        // imgId만 넘어온 경우 (이미지를 등록한 게시글)
        if (multipartFileList == null) {
            Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                    new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            return new UpdatePostResponseDto(post);
        }

        // ImgId와 파일이 같이 온 경우 (이미지를 등록하고, 이미지를 수정하는 게시글)
        // 수정할 Post 가져오기
        Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        if (post.getPostImageList().get(0).getId() != requestDto.getImgId()) {
            throw new CustomException(ErrorCode.NOT_YOUR_IMG);
        }

        // 이미지 삭제
        PostImage deletePostImage = postImageRepository.findById(requestDto.getImgId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_IMG)
        );
        s3UploadService.deleteFile(deletePostImage.getS3name());
        postImageRepository.delete(deletePostImage);

        // 이미지 추가하게 될 경우 -> 기존에 있던 이미지가 위로 올라오는 현상 방지하려면??


        saveImgToS3(multipartFileList, post, updateImageUrlList, updateImageNameList);

        post.update(requestDto);
        return new UpdatePostResponseDto(post, updateImageUrlList, updateImageNameList);
    }

    private void saveImgToS3(MultipartFile[] multipartFileList, Post post, List<String> updateImageUrlList, List<String> updateImageNameList) throws IOException {
        for (MultipartFile multipartFile : multipartFileList) {
            String filename = UUID.randomUUID() + multipartFile.getOriginalFilename();
            String imageUrl = s3UploadService.saveFile(multipartFile, filename);
            PostImage postImage = PostImage.builder()
                    .url(imageUrl)
                    .imageName(multipartFile.getOriginalFilename())
                    .s3name(filename)
                    .post(post)
                    .build();
            postImageRepository.save(postImage);
            updateImageUrlList.add(imageUrl);
            updateImageNameList.add(multipartFile.getOriginalFilename());
        }
    }

    public void deletePost(Long postId, User user) {
        Post post = postRepository.findByIdAndUser(postId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );

        // 게시글 삭제시, S3 서버에 저장된 이미지도 같이 삭제
        List<PostImage> postImageList = postImageRepository.findAllByPostId(postId);
        for (PostImage postImage : postImageList) {
            // S3name -> UUID 포함된 파일명
            s3UploadService.deleteFile(postImage.getS3name());
        }
        postRepository.delete(post);
    }

    // 성능 향상을 위해, readOnly 설정
    @Transactional
    public GetPostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        return new GetPostResponseDto(post);
    }

    // 메인 페이지 7개 출력
    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getMainPostList() {
        Pageable page = PageRequest.of(0, 7);
        Page<Post> topPosts = postRepository.findByOrderByHitDesc(page);
        return topPosts.stream().map(GetPostListResponseDto::new).toList();
    }

    // new, hot 에 따른 게시글 5개 반환
    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getTypePostList(String type) {
        Pageable page = PageRequest.of(0, 5);
        Page<Post> posts;
        if (type.equals("new")) {
            posts = postRepository.findByOrderByCreatedAtDesc(page);
        } else {
            posts = postRepository.findByOrderByCommentListDesc(page);
        }
        return posts.stream().map(GetPostListResponseDto::new).toList();
    }

    // 검색
    public Page<GetPostListResponseDto> searchPost(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(GetPostListResponseDto::new);
    }

    public Page<GetPostListResponseDto> titleSearchPost(String title, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitleContaining(title, pageable);
//        Page<Comment> getCommentsByPost(Post post, Pageable pageable);
        return postPage.map(GetPostListResponseDto::new);
    }

    // 댓글
//    public Page<Comment> getCommentsByPost (Post post, Pageable pageable) {
//        return commentRepository.findByPost(post, pageable);
//    }

    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getPostByCategoryList(String category) {
        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        Pageable page = PageRequest.of(0, 7);
        Page<Post> topPosts = postRepository.findByCategoryOrderByHitDesc(page, postCategory);
        return topPosts.stream().map(GetPostListResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getTypePostByCategoryList(String category, String type) {
        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        Pageable page = PageRequest.of(0, 5);
        Page<Post> posts;
        if (type.equals("new")) {
            posts = postRepository.findByCategoryOrderByCreatedAtDesc(page, postCategory);
        } else {
            posts = postRepository.findByCategoryOrderByCommentListDesc(page, postCategory);
        }
        return posts.stream().map(GetPostListResponseDto::new).toList();
    }

    // hit 인기수
    @Transactional
    public int updateHit (Long id) {
        return postRepository.updateHit(id);
    }
}
