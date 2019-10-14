package com.ohgnarly.fileripper.integration;

import com.ohgnarly.fileripper.controllers.FileRipperController;
import com.ohgnarly.fileripper.models.FileOutput;
import com.ohgnarly.fileripper.models.FileRipperRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static com.ohgnarly.fileripper.integration.utilities.DataUtility.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.*;

public class FileRipperStepDefs {
    private FileRipperRequest fileRipperRequest;
    private FileRipperController fileRipperController;
    private ResponseEntity<FileOutput> responseEntity;

    @Before
    public void setUp() {
        fileRipperController = new FileRipperController();
        fileRipperRequest = new FileRipperRequest();
    }

    @Given("a file whose fields are separated by a {string}")
    public void aFileWhoseFieldsAreSeparatedByADelimiter(String delimiter) throws Throwable {
        fileRipperRequest.setMultipartFile(createDelimitedMultipartFile(delimiter, false));
        fileRipperRequest.setFileDefinition(createDelimitedFileDefinition(delimiter));
    }

    @Given("a file whose fields are of fixed width")
    public void aFileWhoseFieldsAreOfFixedWidth() throws Throwable {
        fileRipperRequest.setMultipartFile(createFixedMultipartFile(true));
        fileRipperRequest.setFileDefinition(createFixedFileDefinition());
    }

    @Given("a file in xml format")
    public void aFileInXmlFormat() throws Throwable {
        fileRipperRequest.setMultipartFile(createXmlMultipartFile());
        fileRipperRequest.setFileDefinition(createXmlFileDefinition());
    }

    @When("the file is ripped")
    public void theFileIsRipped() throws Throwable {
        responseEntity = fileRipperController.getFileOutput(fileRipperRequest);
    }

    @Then("the file data is returned as json")
    public void theFileDataIsReturnedAsJson() {
        FileOutput fileOutput = responseEntity.getBody();
        assertNotNull(fileOutput);
        assertTrue(isNotBlank(fileOutput.getFileName()));
        assertEquals(4, fileOutput.getRecords().size());
        assertRecords(fileOutput.getRecords());
    }

    private void assertRecords(List<Map<String, Object>> records) {
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
