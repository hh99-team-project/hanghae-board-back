package com.sparta.hanghaeboard.domain.comment.repository;

import com.sparta.hanghaeboard.domain.comment.dto.CommentResponseDto;
import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sparta.hanghaeboard.domain.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    default Page<CommentResponseDto> findAllDtoByPostId(Long postId, Pageable pageable) {
        return findAllByPostId(postId, pageable).map(this::toDto);
    }

    default CommentResponseDto toDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setNickname(comment.getUser().getNickname());
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setModifiedAt(comment.getModifiedAt());
        return dto;
    }

//
//    default CommentResponseDto toDto(Comment comment) {
//        CommentResponseDto dto = new CommentResponseDto();
//        dto.setId(comment.getId());
//        dto.setComment(comment.getComment());
//        dto.setCreatedAt(comment.getCreatedAt());
//        // 다른 필드 설정
//        return dto;
//    }
//
//    default CommentResponseDto toDto(Comment comment) {
//        CommentResponseDto dto = new CommentResponseDto();
//        dto.setId(comment.getId());
//        dto.setComment(comment.getComment());
//        dto.setCreatedAt(comment.getCreatedAt());
//        // 다른 필드 설정
//        return dto;
//    }
    Page<Comment> findByPost(Post post, Pageable pageable);
}
