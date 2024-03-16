package com.sparta.hanghaeboard.domain.post.repository;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndUser(Long postId, User user);

    Page<Post> findByTitleContaining (String title, Pageable pageable);

    Optional<List<Post>> findAllByCategory(String category);

    @Modifying
    @Query("update Post p set p.hit = p.hit + 1 where p.id = :id")
    int updateHit(Long id);
}
