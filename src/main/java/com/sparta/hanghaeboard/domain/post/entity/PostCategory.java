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
        public static final String SOCIETY = "SOCIETY";
        public static final String CULTURE = "CULTURE";
        public static final String SPORTS = "SPORTS";
        public static final String ENTERTAINMENT = "ENTERTAINMENT";
        public static final String IT = "IT";
    }
}
