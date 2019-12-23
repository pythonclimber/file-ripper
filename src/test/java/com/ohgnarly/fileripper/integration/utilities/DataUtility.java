package com.ohgnarly.fileripper.integration.utilities;

import com.google.gson.Gson;
import com.ohgnarly.fileripper.models.FieldDefinition;
import com.ohgnarly.fileripper.models.FileDefinition;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.ohgnarly.fileripper.enums.FileType.*;
import static java.lang.String.format;
import static java.nio.file.Files.*;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.write;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class DataUtility {
    private static Gson gson = new Gson();

    private DataUtility() {
        super();
    }

    public static FileDefinition createDelimitedFileDefinition(String delimiter) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(createFieldDefinition("name", null, null));
        fieldDefinitions.add(createFieldDefinition("age", null, null));
        fieldDefinitions.add(createFieldDefinition("dob", null, null));

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setFileMask("Valid-Delimited-*.txt");
        fileDefinition.setFileType(DELIMITED);
        fileDefinition.setDelimiter(delimiter);
        fileDefinition.setHasHeader(false);
        fileDefinition.setFieldDefinitions(fieldDefinitions);

        return fileDefinition;
    }

    public static FileDefinition createFixedFileDefinition() {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(createFieldDefinition("name", 0, 20));
        fieldDefinitions.add(createFieldDefinition("age", 20, 5));
        fieldDefinitions.add(createFieldDefinition("dob", 25, 10));

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setFileMask("Valid-Fixed-*.txt");
        fileDefinition.setFileType(FIXED);
        fileDefinition.setHasHeader(true);
        fileDefinition.setFieldDefinitions(fieldDefinitions);

        return fileDefinition;
    }

    public static FileDefinition createXmlFileDefinition() {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(createFieldDefinition("name", null, null));
        fieldDefinitions.add(createFieldDefinition("age", null, null));
        fieldDefinitions.add(createFieldDefinition("dob", null, null));

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setFileType(XML);
        fileDefinition.setFileMask("Valid-Xml-*.txt");
        fileDefinition.setHasHeader(false);
        fileDefinition.setRecordXmlElement("Person");
        fileDefinition.setFieldDefinitions(fieldDefinitions);

        return fileDefinition;
    }

    public static File createDelimitedFile(String delimiter, boolean hasHeader) throws Throwable {
        List<String> lines = new ArrayList<>();
        if (hasHeader) {
            lines.add(join(asList("name", "age", "dob"), delimiter));
        }
        lines.add(join(asList("Aaron", "39", "09/04/1980"), delimiter));
        lines.add(join(asList("Gene", "61", "01/15/1958"), delimiter));
        lines.add(join(asList("Alexander", "4", "11/22/2014"), delimiter));
        lines.add(join(asList("Mason", "12", "04/13/2007"), delimiter));

        Path path = createTempFile("Valid-Delimited-", ".txt");
        return write(path, join(lines, "\n").getBytes()).toFile();
    }

    public static MultipartFile createDelimitedMultipartFile(String delimiter, boolean hasHeader) throws Throwable {
        File file = createDelimitedFile(delimiter, hasHeader);
        return new MockMultipartFile(file.getName(), readAllBytes(file.toPath()));
    }

    public static File createFixedFile(boolean hasHeader) throws Throwable {
        List<String> lines = new ArrayList<>();
        String format = "%s%s%s";
        if (hasHeader) {
            lines.add(format(format, rightPad("name", 20), rightPad("age", 5),
                    rightPad("dob", 10)));
        }
        lines.add(format(format, rightPad("Aaron", 20), rightPad("39", 5), "09/04/1980"));
        lines.add(format(format, rightPad("Gene", 20), rightPad("61", 5), "01/15/1958"));
        lines.add(format(format, rightPad("Alexander", 20), rightPad("4", 5), "11/22/2014"));
        lines.add(format(format, rightPad("Mason", 20), rightPad("12", 5), "04/13/2007"));

        Path path = createTempFile("Valid-Fixed-", ".txt");
        return write(path, join(lines, "\n").getBytes()).toFile();
    }

    public static MultipartFile createFixedMultipartFile(boolean hasHeader) throws Throwable {
        File file = createFixedFile(hasHeader);
        return new MockMultipartFile(file.getName(), readAllBytes(file.toPath()));
    }

    public static File createXmlFile() throws Throwable {
        List<String> lines = new ArrayList<>();
        lines.add("<People>");
        lines.addAll(createXmlRecord(asList("Aaron", "39", "09/04/1980")));
        lines.addAll(createXmlRecord(asList("Gene", "61", "01/15/1958")));
        lines.addAll(createXmlRecord(asList("Alexander", "4", "11/22/2014")));
        lines.addAll(createXmlRecord(asList("Mason", "12", "04/13/2007")));
        lines.add("</People>");

        Path path = createTempFile("Valid-Xml-", ".xml");
//        Path path2 = Files.createFile(Paths.get("/users/asmitty/workspace/hello.xml"));
//        write(path2, join(lines, "\n").getBytes());
        return write(path, join(lines, "").getBytes()).toFile();
    }

    public static MultipartFile createXmlMultipartFile() throws Throwable {
        File file = createXmlFile();
        return new MockMultipartFile(file.getName(), readAllBytes(file.toPath()));
    }

    public static FieldDefinition createFieldDefinition(String fieldName, Integer startPosition, Integer fieldLength) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setFieldName(fieldName);
        fieldDefinition.setStartPosition(startPosition);
        fieldDefinition.setFieldLength(fieldLength);
        return fieldDefinition;
    }

    private static List<String> createXmlRecord(List<String> fields) {
        List<String> lines = new ArrayList<>();
        lines.add("\t<Person>");
        lines.add(format("\t\t<name>%s</name>", fields.get(0)));
        lines.add(format("\t\t<age>%s</age>", fields.get(1)));
        lines.add(format("\t\t<dob>%s</dob>", fields.get(2)));
        lines.add("\t</Person>");
        return lines;
    }
}
