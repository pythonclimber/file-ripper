package com.ohgnarly.fileripper

import cucumber.api.PendingException
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Assert.*
import org.xml.sax.SAXParseException
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.nio.file.Files
import java.nio.file.Files.*
import java.nio.file.Paths
import java.nio.file.Paths.*
class FileRipperStepDefs {
    private lateinit var fileOutputList: List<FileOutput>
    private lateinit var fileRipper: FileRipper
    private lateinit var fileDefinition: FileDefinition
    private lateinit var fileResultList: List<FileResult<Person>>
    private lateinit var mockFileRepository: FileRepository
    private lateinit var throwableAssert: AbstractThrowableAssert<*, out Throwable>
    private var file: File = File("Hello-${System.currentTimeMillis()}.txt")

    @Before
    fun setUp() {
        println(get("").toAbsolutePath())
        mockFileRepository = mockk()
        val mockFileMover = mockk<FileMover>()
        val mockDataExporter = mockk<DataExporter>()
        fileRipper = FileRipper(mockFileRepository, mockFileMover)

        if (!exists(get("files"))) {
            createDirectory(get("files"))
        }
    }

    @After
    fun tearDown() {
        File("files/").listFiles().forEach { file ->
            file.deleteRecursively()
        }
    }

    @Given("a file whose fields are separated by a {string}")
    fun aFileWhoseFieldsAreSeparatedByADelimiter(delimiter: String) {
        file = buildDelimitedFile(delimiter, false)
        every { mockFileRepository.getFiles(any(), any()) } returns listOf(file)
        fileDefinition = buildDelimitedFileDefinition(delimiter)
    }

    @Given("a file whose fields are of fixed width")
    fun aFileWhoseFieldsAreOfFixedWidth() {
        file = buildFixedFile(true)
        every { mockFileRepository.getFiles(any(), any()) } returns listOf(file)
        fileDefinition = buildFixedFileDefinition()
    }

    @Given("a file in xml format")
    fun aFileInXmlFormat() {
        file = buildXmlFile()
        every { mockFileRepository.getFiles(any(), any()) } returns listOf(file)
        fileDefinition = buildXmlFileDefinition()
    }

    @When("the file is ripped as output")
    fun theFileIsRippedAsOutput() {
        fileOutputList = fileRipper.findAndRipFiles(fileDefinition)
    }

    @When("the file is ripped as result")
    fun theFileIsRippedAsResult() {
        fileResultList = fileRipper.findAndRipFiles(fileDefinition) { fields: Map<String, String> -> buildPerson(fields) }
    }

    @Then("the file data is returned as map")
    fun theFileDataIsReturnedAsMap() {
        assertEquals(1, fileOutputList.size.toLong())
        val fileOutput = fileOutputList[0]
        assertNotNull(fileOutput)
        assertTrue(StringUtils.isNotBlank(fileOutput.fileName))
        assertEquals(4, fileOutput.records.size.toLong())
        assertRecords(fileOutput.records)
    }

    @Then("the file data is returned as object")
    fun theFileDataIsReturnedAsObject() {
        assertEquals(1, fileResultList.size.toLong())
        val fileResult = fileResultList[0]
        assertNotNull(fileResult)
        assertTrue(StringUtils.isNotBlank(fileResult.fileName))
        assertEquals(4, fileResult.records.size.toLong())
        assertPeople(fileResult.records)
    }

    @Given("fixed files stored on file system")
    fun filesStoredOnFileSystem() {
        buildFixedFile(true)
    }

    @Given("a fixed file definition")
    fun aFixedFileDefinition() {
        fileDefinition = buildFixedFileDefinition()
    }

    @Given("file definition has input directory, file mask")
    fun fileDefinitionHasInputDirectoryFileMask() {
        fileDefinition.inputDirectory = "${get("").toAbsolutePath()}/files"
        fileDefinition.fileMask = "Valid-Fixed-*.txt"
    }

    @Given("file definition has completed directory")
    fun fileDefinitionHasCompletedDirectory() {
        fileDefinition.completedDirectory = "${fileDefinition.inputDirectory}/completed"
    }

    @Given("a xml file definition")
    fun aXmlFileDefinition() {
        fileDefinition = buildXmlFileDefinition()
    }

    @Given("a delimited file definition with {string}")
    fun aDelimitedFileDefinitionWith(delimiter: String) {
        fileDefinition = buildDelimitedFileDefinition(delimiter)
    }

    @When("the files are found and ripped")
    fun theFilesAreFoundAndRipped() {
        fileRipper = FileRipper()
        fileOutputList = fileRipper.findAndRipFiles(fileDefinition)
    }

    @When("the file is ripped")
    fun theFileIsRipped() {
        throwableAssert = assertThatThrownBy { fileRipper.ripFile(file, fileDefinition) }
    }

    @Then("data is returned for all files")
    fun dataIsReturnedForAllFiles() {
        fileOutputList.forEach {
            assertThat(it.fileName).matches("Valid-[A-Za-z0-9\\-]*.txt")
            assertRecords(it.records)
        }
    }

    @Then("files are still in input directory")
    fun filesAreStillInInputDirectory() {
        fileOutputList.forEach {
            val filePath = get("${get("").toAbsolutePath()}/files/${it.fileName}")
            exists(filePath)
        }
    }

    @Then("files are in completed directory")
    fun filesAreInCompletedDirectory() {
        fileOutputList.forEach {
            val filePath = get("${get("").toAbsolutePath()}/files/completed/${it.fileName}")
            exists(filePath)
        }
    }

    @Then("file ripper throws exception")
    fun fileRipperThrowsException() {
        throwableAssert.isInstanceOf(FileRipperException::class.java)
    }

    @Then("exception contains {string}")
    fun exceptionContainsMessage(message: String) {
        throwableAssert.hasMessageFindingMatch(message)
    }

    private fun assertRecords(records: List<Map<String, String>>) {
        assertEquals("Aaron", records[0]["name"])
        assertEquals("39", records[0]["age"])
        assertEquals("09/04/1980", records[0]["dob"])
        assertEquals("Gene", records[1]["name"])
        assertEquals("61", records[1]["age"])
        assertEquals("01/15/1958", records[1]["dob"])
        assertEquals("Alexander", records[2]["name"])
        assertEquals("4", records[2]["age"])
        assertEquals("11/22/2014", records[2]["dob"])
        assertEquals("Mason", records[3]["name"])
        assertEquals("12", records[3]["age"])
        assertEquals("04/13/2007", records[3]["dob"])
    }
}

