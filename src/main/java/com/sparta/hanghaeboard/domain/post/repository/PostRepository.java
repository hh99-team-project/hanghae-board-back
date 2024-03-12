package com.sparta.hanghaeboard.domain.post.repository;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
