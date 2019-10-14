package com.ohgnarly.fileripper.services;

import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.readAllLines;

public abstract class FileService {
    FileDefinition fileDefinition;

    FileService(FileDefinition fileDefinition) {
        this.fileDefinition = fileDefinition;
    }

    public abstract FileOutput processFile(File file) throws FileRipperException;
}
