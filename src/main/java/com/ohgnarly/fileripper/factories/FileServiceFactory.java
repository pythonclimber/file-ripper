package com.ohgnarly.fileripper.factories;

import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.services.FileService;
import com.ohgnarly.fileripper.services.FlatFileService;
import com.ohgnarly.fileripper.services.XmlFileService;
import org.springframework.stereotype.Component;

@Component
public class FileServiceFactory {
    public FileService createFileService(FileDefinition fileDefinition) {
        switch (fileDefinition.getFileType()) {
            case DELIMITED:
            case FIXED:
                return new FlatFileService(fileDefinition);
            case XML:
                return new XmlFileService(fileDefinition);
            default:
                throw new IllegalArgumentException("Invalid file type provided");
        }
    }
}
