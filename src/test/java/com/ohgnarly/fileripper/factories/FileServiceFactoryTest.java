package com.ohgnarly.fileripper.factories;

import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.services.FileService;
import com.ohgnarly.fileripper.services.FlatFileService;
import com.ohgnarly.fileripper.services.XmlFileService;
import org.junit.Before;
import org.junit.Test;

import static com.ohgnarly.fileripper.integration.utilities.DataUtility.*;
import static org.junit.Assert.assertTrue;

public class FileServiceFactoryTest {
    private FileServiceFactory fileServiceFactory;

    @Before
    public void setUp() {
        fileServiceFactory = new FileServiceFactoryImpl();
    }

    @Test
    public void testCreateFileService_GivenDelimitedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        FileDefinition fileDefinition = createDelimitedFileDefinition(",");

        //act
        FileService fileService = fileServiceFactory.createFileService(fileDefinition);

        //assert
        assertTrue(fileService instanceof FlatFileService);
    }

    @Test
    public void testCreateFileService_GivenFixedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        FileDefinition fileDefinition = createFixedFileDefinition();

        //act
        FileService fileService = fileServiceFactory.createFileService(fileDefinition);

        //assert
        assertTrue(fileService instanceof FlatFileService);
    }

    @Test
    public void testCreateFileService_GivenXmlFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        FileDefinition fileDefinition = createXmlFileDefinition();

        //act
        FileService fileService = fileServiceFactory.createFileService(fileDefinition);

        //assert
        assertTrue(fileService instanceof XmlFileService);
    }
}