package com.ohgnarly.fileripper.models;

import com.ohgnarly.fileripper.enums.FileType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class FileDefinition {
    private String fileMask;
    private FileType fileType;
    private boolean hasHeader;
    private String delimiter;
    private List<FieldDefinition> fieldDefinitions;
    private String recordXmlElement;
}
