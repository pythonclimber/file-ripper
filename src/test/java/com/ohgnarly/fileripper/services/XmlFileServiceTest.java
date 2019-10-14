package com.ohgnarly.fileripper.services;

import com.ohgnarly.fileripper.exceptions.FileRipperException;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;
import org.junit.Test;

import java.io.File;

import static com.ohgnarly.fileripper.integration.utilities.DataUtility.*;
import static org.junit.Assert.*;

public class XmlFileServiceTest {
    private XmlFileService xmlFileService;


    @Test(expected = FileRipperException.class)
    public void testProcessFile_GivenFieldNotInFile_ShouldThrowFileRipperException() throws Throwable {
        //arrange
        File file = createXmlFile();
        FileDefinition fileDefinition = createXmlFileDefinition();
        fileDefinition.getFieldDefinitions()
                .add(createFieldDefinition("address", null, null));

        xmlFileService = new XmlFileService(fileDefinition);

        //act
        xmlFileService.processFile(file);
    }
}