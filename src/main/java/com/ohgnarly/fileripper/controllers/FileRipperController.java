package com.ohgnarly.fileripper.controllers;

import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.factories.FileServiceFactory;
import com.ohgnarly.fileripper.models.FileOutput;
import com.ohgnarly.fileripper.models.FileRipperRequest;
import com.ohgnarly.fileripper.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class FileRipperController {
    private FileServiceFactory fileServiceFactory;

    public FileRipperController() {
        this(new FileServiceFactory());
    }

    @Autowired
    public FileRipperController(FileServiceFactory fileServiceFactory) {
        this.fileServiceFactory = fileServiceFactory;
    }

    @PostMapping(value = "/rip", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<FileOutput> getFileOutput(FileRipperRequest request) throws FileRipperException {
        FileService fileService = fileServiceFactory.createFileService(request.getFileDefinition());
        FileOutput fileOutput = fileService.processFile(convertFile(request.getMultipartFile()));
        fileOutput.setFileName(request.getMultipartFile().getName());
        return new ResponseEntity<>(fileOutput, OK);
    }

    private File convertFile(MultipartFile multipartFile) throws FileRipperException {
        try {
            File file = new File(multipartFile.getName());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            return file;
        } catch (IOException ex) {
            throw new FileRipperException(ex);
        }
    }
}
