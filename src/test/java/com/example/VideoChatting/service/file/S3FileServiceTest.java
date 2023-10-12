package com.example.VideoChatting.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.VideoChatting.dto.FileUploadDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {
    @Mock
    private FileService fileUploadService;
    @Mock
    AmazonS3 amazonS3;
    @Mock
    S3FileService s3FileService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3 base URL
    @Value("${cloud.aws.s3.bucketUrl}")
    private String baseUrl;

    @Test
    @DisplayName("채팅시 파일 업로드 테스트")
    void uploadFile(){
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Mock file content".getBytes());
        String transaction = "transaction123";
        String roomId = "room123";
        String expectedKey = roomId + "/" + transaction + "/test.txt";
        String expectedS3DataUrl = baseUrl+ "/" + expectedKey;

        when(s3FileService.uploadFile(file, transaction, roomId))
                .thenReturn(new FileUploadDto(file, file.getOriginalFilename(), transaction, roomId, expectedS3DataUrl, expectedKey));

        FileUploadDto fileUploadDto = s3FileService.uploadFile(file, transaction, roomId);

        Assertions.assertThat(fileUploadDto.getFile().getOriginalFilename()).isEqualTo("test.txt");
        Assertions.assertThat(fileUploadDto.getFile().getName()).isEqualTo("file");
    }

}