package com.sparta.hanghaeboard.domain.comment.repository;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
