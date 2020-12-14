package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals


class FileServiceCompanionTest {
    @Test
    fun testCreateDelimitedFileService() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition(",")

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFixedFileService() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateXmlFileService() {
        //arrange
        val fileDefinition = buildXmlFileDefinition()

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is XmlFileService)
    }

    @Test
    fun testInvalidFileType() {
        //arrange
        val fileDefinition = FileDefinition()

        //act
        assertThrows<UninitializedPropertyAccessException> { FileService.create(fileDefinition) }
    }
}


class FixedFileServiceTest {
    private lateinit var flatFileService: FlatFileService
    
    @Test
    fun testProcessFixedFile() {
        //arrange
        val file = buildFixedFile(true)
        flatFileService = FixedFileService(buildFixedFileDefinition())

        //act
        val fileOutput = flatFileService.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }
    
    @Test
    fun testInvalidFieldLengthOnLastFile() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions[2].fieldLength = 11
        flatFileService = FixedFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act and assert
        assertThrows<FileRipperException> { flatFileService.processFile(file) }
    }

    @Test
    fun testFileIsMissingConfiguredField() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", 36, 0, null))
        flatFileService = FixedFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act
        assertThrows<FileRipperException> { flatFileService.processFile(file) }
    }
}


class XmlFileServiceTest {
    private lateinit var xmlFileService: XmlFileService

    @Test
    fun testProcessXmlFile() {
        //arrange
        val file = buildXmlFile()
        xmlFileService = XmlFileService(buildXmlFileDefinition())

        //act
        val fileOutput = xmlFileService.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test
    fun testDataElementNotFoundInRecord() {
        //arrange
        val file = buildXmlFile()

        val fileDefinition = buildXmlFileDefinition()
        fileDefinition.fieldDefinitions
                .add(buildFieldDefinition("address", null, null, null))
        xmlFileService = XmlFileService(fileDefinition)

        //act
        assertThrows<FileRipperException> { xmlFileService.processFile(file) }
    }
}


class DelimitedFileServiceTest {
    private lateinit var delimitedFileService: DelimitedFileService

    @Test
    fun testProcessDelimitedFile() {
        //arrange
        val file = buildDelimitedFile()
        delimitedFileService = DelimitedFileService(buildDelimitedFileDefinition())

        //act
        val fileOutput = delimitedFileService.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test
    fun testInvalidNumberOfFieldsConfigured() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition()
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", null, null, 3))
        delimitedFileService = DelimitedFileService(fileDefinition)

        val file = buildDelimitedFile()

        //act
        assertThrows<FileRipperException> { delimitedFileService.processFile(file) }
    }

    @Test
    fun testMissingDelimiter() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition()
        val file = buildDelimitedFile()

        fileDefinition.delimiter = ""

        delimitedFileService = DelimitedFileService(fileDefinition)

        //act
        var assertThrows = Assertions.assertThatThrownBy { delimitedFileService.processFile(file) }

        //assert
        assertThrows.isInstanceOf(FileRipperException::class.java)
        assertThrows.hasMessageContaining("File definition is missing the following fields")
    }

    private fun buildDelimitedFile(): File {
        val lines = mutableListOf<String>().apply {
            add(StringUtils.join(listOf("Aaron", "09/04/1980", "39"), "|"))
            add(StringUtils.join(listOf("Gene", "01/15/1958", "61"), "|"))
            add(StringUtils.join(listOf("Alexander", "11/22/2014", "4"), "|"))
            add(StringUtils.join(listOf("Mason", "04/13/2007", "12"), "|"))
        }

        return writeFile("Valid-Delimited-", ".txt", lines)
    }

    private fun buildDelimitedFileDefinition(): FileDefinition {
        val fieldDefinitions = mutableListOf<FieldDefinition>().apply {
            add(buildFieldDefinition("name", null, null, 0))
            add(buildFieldDefinition("age", null, null, 2))
            add(buildFieldDefinition("dob", null, null, 1))
        }

        return FileDefinition().apply {
            this.delimiter = "|"
            fileType = FileType.DELIMITED
            hasHeader = false
            this.fieldDefinitions = fieldDefinitions
        }
    }
}