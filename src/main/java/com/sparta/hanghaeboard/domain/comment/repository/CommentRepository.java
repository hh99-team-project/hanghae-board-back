package com.sparta.hanghaeboard.domain.comment.repository;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);
}
