package com.ohgnarly.fileripper.services;

import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.models.FileDefinition;
import org.junit.Test;

import java.io.File;

import static com.ohgnarly.fileripper.integration.utilities.DataUtility.*;

public class FlatFileServiceTest {
    private FlatFileService flatFileService;

    @Test(expected = FileRipperException.class)
    public void testProcessFile_GivenDelimitedFile_AndInvalidFileFormat_ShouldThrowIOException() throws Throwable {
        //arrange
        FileDefinition fileDefinition = createDelimitedFileDefinition("|");
        fileDefinition.getFieldDefinitions().add(createFieldDefinition("address", null, null));
        File file = createDelimitedFile("|", false);
        flatFileService = new FlatFileService(fileDefinition);

        //act
        flatFileService.processFile(file);
    }

    @Test(expected = FileRipperException.class)
    public void testProcessFile_GivenFixedFile_AndLastFieldTooLong_ShouldThrowIOException() throws Throwable {
        //arrange
        FileDefinition fileDefinition = createFixedFileDefinition();
        fileDefinition.getFieldDefinitions().get(2).setFieldLength(11);
        File file = createFixedFile(true);
        flatFileService = new FlatFileService(fileDefinition);

        //act
        flatFileService.processFile(file);
    }

    @Test(expected = FileRipperException.class)
    public void testProcessFile_GivenTooManyFields_ShouldThrowIOException() throws Throwable {
        //arrange
        FileDefinition fileDefinition = createFixedFileDefinition();
        fileDefinition.getFieldDefinitions().add(createFieldDefinition("address", 36, 0));
        File file = createFixedFile(true);
        flatFileService = new FlatFileService(fileDefinition);

        //act
        flatFileService.processFile(file);
    }
}
