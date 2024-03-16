package com.sparta.hanghaeboard.domain.post.entity;

import lombok.Getter;

@Getter
public enum PostCategory {
    SOCIETY(Category.SOCIETY),
    CULTURE(Category.CULTURE),
    SPORTS(Category.SPORTS),
    ENTERTAINMENT(Category.ENTERTAINMENT),
    IT(Category.IT);

    private final String category;

    PostCategory(String category) {
        this.category = category;
    }

    public static class Category {
        public static final String SOCIETY = "사회";
        public static final String CULTURE = "문화";
        public static final String SPORTS = "스포츠";
        public static final String ENTERTAINMENT = "연예";
        public static final String IT = "IT";
    }
}
