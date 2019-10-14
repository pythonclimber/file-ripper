package com.ohgnarly.fileripper.controllers;

import com.ohgnarly.fileripper.factories.FileServiceFactory;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;
import com.ohgnarly.fileripper.models.FileRipperRequest;
import com.ohgnarly.fileripper.services.FileService;
import org.apache.tomcat.jni.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.class)
public class FileRipperControllerTest {
    @InjectMocks
    private FileRipperController fileRipperController;

    @Mock
    private FileServiceFactory mockFileServiceFactory;

    @Mock
    private FileService mockFileService;

    @Test
    public void testGetFileOutput_GivenEmptyFile_ShouldReceiveGenericResponse() throws Throwable {
        //arrange
        MultipartFile file = new MockMultipartFile("Hello-World.txt", (byte[])null);
        FileDefinition fileDefinition = new FileDefinition();
        FileRipperRequest request = new FileRipperRequest();
        request.setFileDefinition(fileDefinition);
        request.setMultipartFile(file);

        when(mockFileService.processFile(any())).thenReturn(createExceptedOutput(file.getName()));
        when(mockFileServiceFactory.createFileService(fileDefinition)).thenReturn(mockFileService);

        //act
        ResponseEntity<FileOutput> response = fileRipperController.getFileOutput(request);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(file.getName(), response.getBody().getFileName());
    }

    private FileOutput createExceptedOutput(String fileName) {
        FileOutput fileOutput = new FileOutput();
        fileOutput.setFileName(fileName);
        fileOutput.setRecords(emptyList());
        return fileOutput;
    }
}