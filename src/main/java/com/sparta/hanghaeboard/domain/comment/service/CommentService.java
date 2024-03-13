package com.sparta.hanghaeboard.domain.comment.service;

import com.sparta.hanghaeboard.domain.comment.dto.CommentRequestDto;
import com.sparta.hanghaeboard.domain.comment.dto.CommentResponseDto;
import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.comment.repository.CommentRepository;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.repository.PostRepository;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    // 댓글 등록 기능
    public CommentResponseDto createComment(Long postId,
                                            CommentRequestDto requestDto,
                                            UserDetailsImpl userDetails) {
        // postId로 특정하여 Post 객체
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));

        // 현재 로그인된 유저를 나타내는 User 객체
        User user = userDetails.getUser();

        // post, user를 매개변수로 하는 toEntity으로  Repository 저장
        Comment comment = commentRepository.save(requestDto.toEntity(user, post));
        // Entity -> ResponseDto
        return new CommentResponseDto(comment);
    }

    // 선택한 강의의 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long postId,
                                            Long commentId,
                                            CommentRequestDto requestDto,
                                            UserDetailsImpl userDetails) {

        // lectureId로 특정하여 Lecture 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));

        // commentId로 특정된 게시글에 대한 댓글 객체
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_COMMENT));

        // 현재 로그인된  user 특정
        User user = userDetails.getUser();

        // commentId를 작성한 userId 특정
        Long commentAuthorId = comment.getUser().getId();

        // 현재 로그인된 유저와 작성자 일치 여부 확인
        validateCommentAuthor(user.getId(), commentAuthorId);

        comment.updateComment(requestDto);

        return new CommentResponseDto(comment);
    }

    // 선택한 강의의 선택한 댓글 삭제 // + 댓글 등록한 회원만 수정 가능
    @Transactional
    public void deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        // postId로 특정하여 Post 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));

        // DB에서 commentId로 댓글 정보 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_COMMENT));

        // 현재 로그인된  user 특정
        User user = userDetails.getUser();

        // commentId를 작성한 userId 특정
        Long commentAuthorId = comment.getUser().getId();

        // 현재 로그인된 유저와 작성자 일치 여부 확인
        validateCommentAuthor(user.getId(), commentAuthorId);

        // 댓글 삭제
        commentRepository.delete(comment);
    }

    private void validateCommentAuthor(Long loggedInUserId, Long commentAuthorId) {
        if (!loggedInUserId.equals(commentAuthorId)) {
            throw new CustomException(ErrorCode.AUTHORITY_ACCESS);
        }
    }


}