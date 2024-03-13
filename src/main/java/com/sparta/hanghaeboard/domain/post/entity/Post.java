package com.sparta.hanghaeboard.domain.post.entity;

import com.sparta.hanghaeboard.domain.comment.entity.Comment;
import com.sparta.hanghaeboard.domain.post.dto.PostRequestDto.UpdatePostRequestDto;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "posts")
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String contents;

    private String category;

    // 연결 필요
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    // Comments 연결 필요
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public void update(UpdatePostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.category = requestDto.getCategory();
    }
}
