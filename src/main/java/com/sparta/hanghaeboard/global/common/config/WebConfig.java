//package com.sparta.hanghaeboard.global.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 모든 경로를 설정해 줄 것이기 때문에 /**로 설정
//                .allowedOrigins("*") // "*"은 위험하니 명시하기
////                .allowedOrigins("http://3.34.179.179:8080", "http://hanghae-board-front-sean.s3-website.ap-northeast-2.amazonaws.com/", "http://localhost:3000") // "*"은 위험하니 명시하기
//                .allowedMethods("*") // 모든 http method를 허용할 것으로 *로 설정
//                .allowedHeaders("*")
//                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
//            //.allowCredentials
//    }
//}