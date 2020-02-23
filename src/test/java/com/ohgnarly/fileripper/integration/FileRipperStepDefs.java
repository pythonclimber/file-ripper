package com.ohgnarly.fileripper.integration;

import com.ohgnarly.fileripper.FileRipper;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;
import com.ohgnarly.fileripper.models.FileResult;
import com.ohgnarly.fileripper.repositories.FileRepository;
import com.ohgnarly.fileripper.movers.FileMover;
import com.ohgnarly.fileripper.testhelpers.ObjectBuildersKt;
import com.ohgnarly.fileripper.testhelpers.Person;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.ohgnarly.fileripper.testhelpers.AssertHelpersKt.assertPeople;
import static com.ohgnarly.fileripper.testhelpers.FileBuildersKt.*;
import static com.ohgnarly.fileripper.testhelpers.FileDefinitionBuildersKt.*;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileRipperStepDefs {
    private List<FileOutput> fileOutputList;
    private FileRipper fileRipper;
    private FileDefinition fileDefinition;
    private List<FileResult<Person>> fileResultList;
    private FileRepository mockFileRepository;

    @Before
    public void setUp() {
        mockFileRepository = mock(FileRepository.class);
        FileMover mockFileMover = mock(FileMover.class);
        fileRipper = new FileRipper(mockFileRepository, mockFileMover);
    }

    @Given("a file whose fields are separated by a {string}")
    public void aFileWhoseFieldsAreSeparatedByADelimiter(String delimiter) {
        File file = buildDelimitedFile(delimiter, false);
        when(mockFileRepository.getFiles(anyString(), anyString())).thenReturn(singletonList(file));
        fileDefinition = buildDelimitedFileDefinition(delimiter);
    }

    @Given("a file whose fields are of fixed width")
    public void aFileWhoseFieldsAreOfFixedWidth() {
        File file = buildFixedFile(true);
        when(mockFileRepository.getFiles(anyString(), anyString())).thenReturn(singletonList(file));
        fileDefinition = buildFixedFileDefinition();
    }

    @Given("a file in xml format")
    public void aFileInXmlFormat() {
        File file = buildXmlFile();
        when(mockFileRepository.getFiles(anyString(), anyString())).thenReturn(singletonList(file));
        fileDefinition = buildXmlFileDefinition();
    }

    @When("the file is ripped as output")
    public void theFileIsRippedAsOutput() {
        fileOutputList = fileRipper.findAndRipFiles(fileDefinition);
    }

    @When("the file is ripped as result")
    public void theFileIsRippedAsResult() {
        fileResultList = fileRipper.findAndRipFiles(fileDefinition, ObjectBuildersKt::buildPerson);
    }

    @Then("the file data is returned as map")
    public void theFileDataIsReturnedAsMap() {
        assertEquals(1, fileOutputList.size());
        FileOutput fileOutput = fileOutputList.get(0);
        assertNotNull(fileOutput);
        assertTrue(isNotBlank(fileOutput.getFileName()));
        assertEquals(4, fileOutput.getRecords().size());
        assertRecords(fileOutput.getRecords());
    }

    @Then("the file data is returned as object")
    public void theFileDataIsReturnedAsObject() {
        assertEquals(1, fileResultList.size());
        FileResult<Person> fileResult = fileResultList.get(0);
        assertNotNull(fileResult);
        assertTrue(isNotBlank(fileResult.getFileName()));
        assertEquals(4, fileResult.getRecords().size());
        assertPeople(fileResult.getRecords());
    }

    private void assertRecords(List<Map<String, String>> records) {
        assertEquals("Aaron", records.get(0).get("name"));
        assertEquals("39", records.get(0).get("age"));
        assertEquals("09/04/1980", records.get(0).get("dob"));
        assertEquals("Gene", records.get(1).get("name"));
        assertEquals("61", records.get(1).get("age"));
        assertEquals("01/15/1958", records.get(1).get("dob"));
        assertEquals("Alexander", records.get(2).get("name"));
        assertEquals("4", records.get(2).get("age"));
        assertEquals("11/22/2014", records.get(2).get("dob"));
        assertEquals("Mason", records.get(3).get("name"));
        assertEquals("12", records.get(3).get("age"));
        assertEquals("04/13/2007", records.get(3).get("dob"));

    }
}
