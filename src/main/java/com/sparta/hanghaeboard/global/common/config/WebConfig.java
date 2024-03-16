//package com.sparta.hanghaeboard.global.common.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:8080", "http://localhost:80", "http://localhost:3000") // 허용할 출처
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP method
//                .allowCredentials(true) // 쿠키 인증 요청 허용
//                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
//    }
//}

// 이 설정으로 CORS를 해결하신 조의 코드를 받아왔습니다.
// 나중에 시간이 된다면, 테스트해보겠습니다!