package com.ohgnarly.fileripper.services;

import com.ohgnarly.fileripper.enums.FileType;
import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.models.FieldDefinition;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ohgnarly.fileripper.enums.FileType.*;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static org.apache.commons.lang3.StringUtils.split;

public class FlatFileService extends FileService {
    public FlatFileService(FileDefinition fileDefinition) {
        super(fileDefinition);
    }

    @Override
    public FileOutput processFile(File file) throws FileRipperException {
        try {
            FileOutput fileOutput = new FileOutput();
            fileOutput.setFileName(file.getName());
            fileOutput.setRecords(processLines(readAllLines(file.toPath())));
            return fileOutput;
        } catch (IOException ex) {
            throw new FileRipperException("Error reading provided file", ex);
        }
    }

    private List<Map<String, Object>> processLines(List<String> lines) throws FileRipperException {
        List<Map<String, Object>> records = new ArrayList<>();
        if (fileDefinition.isHasHeader()) { //if list has header, remove first line in list
            lines.remove(0);
        }

        for (String line : lines) {
            if (fileDefinition.getFileType() == DELIMITED) {
                records.add(processDelimitedLine(line));
            } else if (fileDefinition.getFileType() == FIXED) {
                records.add(processFixedLine(line));
            }
        }
        return records;
    }

    private Map<String,Object> processDelimitedLine(String line) throws FileRipperException {
        String[] fields = split(line, fileDefinition.getDelimiter());
        if (fields.length < fileDefinition.getFieldDefinitions().size()) {
            throw new FileRipperException(format("Record '%s' has invalid number of fields", line));
        }

        Map<String, Object> record = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fileDefinition.getFieldDefinitions().get(i).getFieldName();
            String fieldValue = fields[i];


            record.put(fieldName, fieldValue);
        }
        return record;
    }

    private Map<String, Object> processFixedLine(String line) throws FileRipperException {
        Map<String,Object> record = new LinkedHashMap<>();
        for (FieldDefinition fieldDefinition : fileDefinition.getFieldDefinitions()) {
            int startPosition = fieldDefinition.getStartPosition();
            int endPosition = fieldDefinition.getStartPosition() + fieldDefinition.getFieldLength();
            if (startPosition > line.length() || endPosition > line.length()) {
                throw new FileRipperException("Invalid line length in fixed width file.");
            }

            String fieldValue = line.substring(fieldDefinition.getStartPosition(), endPosition).trim();
            record.put(fieldDefinition.getFieldName(), fieldValue);
        }
        return record;
    }
}
