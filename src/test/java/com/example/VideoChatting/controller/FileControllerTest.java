package com.example.VideoChatting.controller;

import com.example.VideoChatting.dto.FileUploadDto;
import com.example.VideoChatting.service.file.S3FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
    @Mock
    private S3FileService fileService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }
    @Test
    @DisplayName("채팅방 파일 업로드 테스트")
    void uploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        // Mocking the behavior of the fileService.uploadFile method
        FileUploadDto mockFileUploadDto = new FileUploadDto();
        mockFileUploadDto.setFileDir("/images");
        mockFileUploadDto.setOriginFileName("test.txt");
        mockFileUploadDto.setChatRoom("방123");


        ResultActions perform = mockMvc.perform(multipart("/s3/upload")
                .file(file)
                .param("roomId", "12345")
                .contentType(MediaType.APPLICATION_JSON));
        System.out.println("perform = " + perform);
        perform.andExpect(status().isOk());


    }

    @Test
    @DisplayName("채팅방 파일 다운로드 테스트")
    void downlaod() throws Exception {
        String fileName = "example.txt";
        String fileDir = "/path/to/files";  // Replace with your actual file directory

        byte[] fileContent = "Sample file content".getBytes(); // Replace with actual file content
        ResponseEntity<byte[]> expectedResponse = new ResponseEntity<>(fileContent, HttpStatus.OK);
        when(fileController.downlaod(fileDir, fileName)).thenReturn(expectedResponse);


        // When
        ResponseEntity<byte[]> downlaod = fileController.downlaod(fileDir, fileName);

        // Then
        assertThat(expectedResponse).isEqualTo(downlaod);
    }
}