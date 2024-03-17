package com.sparta.hanghaeboard.domain.post.service;

import com.sparta.hanghaeboard.domain.comment.dto.CommentResponseDto;
import com.sparta.hanghaeboard.domain.comment.repository.CommentRepository;
import com.sparta.hanghaeboard.domain.post.dto.PagingDataResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.CreatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.UpdatePostRequestDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.CreatePostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostListResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.GetPostResponseDto;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto.UpdatePostResponseDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
    public Pair<GetPostResponseDto, Map<String, Object>> getPost(Long postId, int page) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );

        int pageNumber = Math.max(page - 1, 0); // 페이지 번호는 사용자 입력과 동일하게 설정
        int pageSize = 10; // 페이지당 표시할 데이터의 개수

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        // CommentRepository에서 Defalut 메서드 추가하여 CommentResponseDto의 Page 객체 반환하는
        Page<CommentResponseDto> commentsPage = commentRepository.findAllDtoByPostId(postId, pageable);


        // 현재 페이지 및 페이지 범위 계산
        int nowPage = commentsPage.getPageable().getPageNumber() + 1; // 0부터 시작하므로 +1
        int startPage = Math.max(nowPage - 4, 1); // 현재 페이지 기준으로 시작 페이지 설정
        int endPage = Math.min(nowPage + 5, commentsPage.getTotalPages()); // 현재 페이지 기준으로 끝 페이지 설정

        // 이전 페이지 및 다음 페이지의 존재 여부 계산
        boolean hasNext = commentsPage.hasNext();
        boolean hasPrev = commentsPage.hasPrevious();


        // 응답으로 반환할 데이터를 Map에 담음
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("resultCode", "SUCCESS"); // 결과 코드 설정
        responseBody.put("message", "검색 성공"); // 메시지 설정
        responseBody.put("nowPage", nowPage); // 현재 페이지 번호
        responseBody.put("startPage", startPage); // 시작 페이지 번호
        responseBody.put("endPage", endPage); // 끝 페이지 번호
        responseBody.put("hasNext", hasNext); // 다음 페이지 존재 여부
        responseBody.put("hasPrev", hasPrev); // 이전 페이지 존재 여부
        responseBody.put("data", commentsPage.getContent()); // 현재 페이지의 포스트 리스트

        return Pair.of(new GetPostResponseDto(post), responseBody);
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
    public PagingDataResponseDto<?> getTypePostList(String type, int num) {
        int pageNumber = Math.max(num - 1, 0);
        Pageable page = PageRequest.of(pageNumber, 5);
        Page<Post> posts;
        if (type.equals("new")) {
            posts = postRepository.findByOrderByCreatedAtDesc(page);
        } else {
            posts = postRepository.findByOrderByCommentListDesc(page);
        }
        int nowPage = posts.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1); // 현재 페이지 기준으로 시작 페이지 설정
        int endPage = Math.min(nowPage + 5, posts.getTotalPages()); // 현재 페이지 기준으로 끝 페이지 설정
        boolean hasNext = posts.hasNext();
        boolean hasPrev = posts.hasPrevious();
        List<GetPostListResponseDto> listResponseDto = posts.stream().map(GetPostListResponseDto::new).toList();
        PagingDataResponseDto<List<GetPostListResponseDto>> responseDto = new PagingDataResponseDto<>(
                listResponseDto, nowPage, startPage, endPage, hasNext, hasPrev
        );
        return responseDto;
    }

    // 검색
    public Page<GetPostListResponseDto> searchPost(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(GetPostListResponseDto::new);
    }

    public Page<GetPostListResponseDto> titleSearchPost(String title, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitleContaining(title, pageable);
        return postPage.map(GetPostListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getPostByCategoryList(String category) {
        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        Pageable page = PageRequest.of(0, 7);
        Page<Post> topPosts = postRepository.findByCategoryOrderByHitDesc(page, postCategory);
        return topPosts.stream().map(GetPostListResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PagingDataResponseDto<?> getTypePostByCategoryList(String category, String type, int num) {
        int pageNumber = Math.max(num - 1, 0);
        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        Pageable page = PageRequest.of(pageNumber, 5);
        Page<Post> posts;
        if (type.equals("new")) {
            posts = postRepository.findByCategoryOrderByCreatedAtDesc(page, postCategory);
        } else {
            posts = postRepository.findByCategoryOrderByCommentListDesc(page, postCategory);
        }
        int nowPage = posts.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1); // 현재 페이지 기준으로 시작 페이지 설정
        int endPage = Math.min(nowPage + 5, posts.getTotalPages()); // 현재 페이지 기준으로 끝 페이지 설정
        boolean hasNext = posts.hasNext();
        boolean hasPrev = posts.hasPrevious();
        List<GetPostListResponseDto> listResponseDto = posts.stream().map(GetPostListResponseDto::new).toList();
        PagingDataResponseDto<List<GetPostListResponseDto>> responseDto = new PagingDataResponseDto<>(
                listResponseDto, nowPage, startPage, endPage, hasNext, hasPrev
        );
        return responseDto;
    }

    // hit 인기수
    @Transactional
    public int updateHit (Long id) {
        return postRepository.updateHit(id);
    }
}
