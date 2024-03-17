package com.sparta.hanghaeboard.domain.post.repository;

import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.post.entity.PostCategory;
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

    @Modifying
    @Query ("update Post p set p.hit = p.hit + 1 where p.id = :id")
    int updateHit (Long id);

    // nativeQuery 방식
//    @Query(value = "SELECT * FROM posts ORDER BY hit DESC LIMIT 7", nativeQuery = true)
//    List<Post> findTop7ByHit();

    // Pageable 방식
    Page<Post> findByOrderByHitDesc(Pageable pageable);

    Page<Post> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByOrderByCommentListDesc(Pageable pageable);

    Page<Post> findByCategoryOrderByHitDesc(Pageable pageable, PostCategory category);

    Page<Post> findByCategoryOrderByCreatedAtDesc(Pageable pageable, PostCategory category);

    Page<Post> findByCategoryOrderByCommentListDesc(Pageable pageable, PostCategory category);

    // QueryDsl 방식 -> 나중에 Refactoring 필요
    Optional<List<Post>> findAllByCategory(String category);

    Page<Post> findById(Long id, Pageable pageable);
}
