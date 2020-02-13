package com.ohgnarly.fileripper.integration;

import com.ohgnarly.fileripper.FileRipper;
import com.ohgnarly.fileripper.models.FileDefinition;
import com.ohgnarly.fileripper.models.FileOutput;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.ohgnarly.fileripper.testhelpers.FileBuildersKt.*;
import static com.ohgnarly.fileripper.testhelpers.FileDefinitionBuildersKt.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.*;

public class FileRipperStepDefs {
    private FileOutput fileOutput;
    private FileRipper fileRipper;
    private FileDefinition fileDefinition;
    private File file;

    @Before
    public void setUp() {
        fileRipper = new FileRipper();
    }

    @Given("a file whose fields are separated by a {string}")
    public void aFileWhoseFieldsAreSeparatedByADelimiter(String delimiter) {
        file = buildDelimitedFile(delimiter, false);
        fileDefinition = buildDelimitedFileDefinition(delimiter);
    }

    @Given("a file whose fields are of fixed width")
    public void aFileWhoseFieldsAreOfFixedWidth() {
        file = buildFixedFile(true);
        fileDefinition = buildFixedFileDefinition();
    }

    @Given("a file in xml format")
    public void aFileInXmlFormat() throws Throwable {
        file = buildXmlFile();
        fileDefinition = buildXmlFileDefinition();
    }

    @When("the file is ripped")
    public void theFileIsRipped() {
        fileOutput = fileRipper.ripFile(file, fileDefinition);
    }

    @Then("the file data is returned as json")
    public void theFileDataIsReturnedAsJson() {
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
