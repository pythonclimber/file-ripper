package com.ohgnarly.fileripper.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class FileRipperRequest {
    private FileDefinition fileDefinition;
    private MultipartFile multipartFile;
}
