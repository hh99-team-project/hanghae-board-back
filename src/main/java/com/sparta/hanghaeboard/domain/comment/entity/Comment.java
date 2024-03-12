package com.sparta.hanghaeboard.domain.comment.entity;

import com.sparta.hanghaeboard.domain.comment.dto.CommentRequestDto;
import com.sparta.hanghaeboard.domain.post.entity.Post;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "comments")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // 댓글내용
    private String comment;

    // 다대일 연관관계
    @ManyToOne(fetch = FetchType.LAZY) // LAZY로 해야 서버 부하가 줄어든다.
    @JoinColumn(name = "post_id")
    private Post post;

    // 여러 개의 댓글이 하나의 사용자에게만 속함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 다대일 연관관계 설정 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }
}
