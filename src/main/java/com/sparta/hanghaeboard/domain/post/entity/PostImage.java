package com.sparta.hanghaeboard.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "post_images")
@ToString
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageName;
    private String s3name;
    private String url;
    // cascade = CascadeType.ALL -> 이거 넣으면 안지워짐...
    // 안넣으면 저장이 안됨...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
