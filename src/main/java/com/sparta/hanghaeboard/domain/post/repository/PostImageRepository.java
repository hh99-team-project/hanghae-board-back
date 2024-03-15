package com.sparta.hanghaeboard.domain.post.repository;


import com.sparta.hanghaeboard.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(Long postId);
}
