package com.sparta.hanghaeboard.global.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "AWS S3 로그")
@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile, String filename) throws IOException {
        // String filename -> UUID 추가된 filename 저장 (중복 방지)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, filename, multipartFile.getInputStream(), metadata);
        // 한글 깨짐 방지
        return URLDecoder.decode(amazonS3.getUrl(bucket, filename).toString(), StandardCharsets.UTF_8);
    }

    public void deleteFile(String originFilename) {
        amazonS3.deleteObject(bucket, originFilename);
    }
}
