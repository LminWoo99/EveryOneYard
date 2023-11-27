package com.example.VideoChatting.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.VideoChatting.dto.FileUploadDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class S3FileServiceTest {
    @Autowired
    private S3FileService s3FileService;
    private String uploadedFileKey;
    private String fileName = "test.txt";

    @BeforeEach
    void setUp() throws Exception {
        // 테스트 파일 생성 및 업로드
        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "text/plain", "test data".getBytes());
        String transaction = UUID.randomUUID().toString();
        String roomId = "testRoom";

        FileUploadDto uploadDto = s3FileService.uploadFile(file, transaction, roomId);
        assertNotNull(uploadDto);

        uploadedFileKey = uploadDto.getFileDir();
    }
    @AfterEach
    void tearDown() {

        s3FileService.deleteFileDir(uploadedFileKey);
    }
    @Test
    void uploadFileTest() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());
        String transaction = UUID.randomUUID().toString();
        String roomId = "testRoom";

        // when
        FileUploadDto uploadDto = s3FileService.uploadFile(file, transaction, roomId);

        // then
        assertNotNull(uploadDto);
        assertEquals("test.txt", uploadDto.getOriginFileName());



    }
    @Test
    void testGetObject() throws IOException {
        // when
        ResponseEntity<byte[]> response = s3FileService.getObject(uploadedFileKey, "test.txt");
        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

}