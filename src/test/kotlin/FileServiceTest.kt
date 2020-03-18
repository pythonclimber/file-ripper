package com.ohgnarly.fileripper

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals

class FileServiceCompanionTest {
    @Test
    fun testCreateFileService_GivenDelimitedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition(",")

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenFixedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenXmlFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildXmlFileDefinition()

        //act
        val fileService = FileService.create(fileDefinition)

        //assert
        Assert.assertTrue(fileService is XmlFileService)
    }

    @Test
    fun testCreateFileService_GivenInvalidFileType_ThrowsIllegalArgumentException() {
        //arrange
        val fileDefinition = FileDefinition()

        //act
        assertThrows<IllegalArgumentException> { FileService.create(fileDefinition) }
    }
}

class FlatFileServiceTest {
    private lateinit var flatFileService: FlatFileService

    @Test
    fun testProcessFile_GivenDelimitedFile_ReturnsFileOutput() {
        //arrange
        val file = buildDelimitedFile("|", false)
        flatFileService = FlatFileService(buildDelimitedFileDefinition("|"))

        //act
        val fileOutput = flatFileService.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test
    fun testProcessFile_GivenFixedWidthFile_ReturnsFileOutput() {
        //arrange
        val file = buildFixedFile(true)
        flatFileService = FlatFileService(buildFixedFileDefinition())

        //act
        val fileOutput = flatFileService.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test
    fun testProcessFile_GivenDelimitedFile_AndInvalidFileFormat_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition("|")
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", null, null))
        flatFileService = FlatFileService(fileDefinition)

        val file = buildDelimitedFile("|", false)

        //act
        assertThrows<FileRipperException> { flatFileService.processFile(file) }
    }

    @Test
    fun testProcessFile_GivenFixedFile_AndLastFieldTooLong_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions[2].fieldLength = 11
        flatFileService = FlatFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act
        assertThrows<FileRipperException> { flatFileService.processFile(file) }
    }

    @Test
    fun testProcessFile_GivenTooManyFields_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", 36, 0))
        flatFileService = FlatFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act
        assertThrows<FileRipperException> { flatFileService.processFile(file) }
    }
}

class XmlFileServiceTest {
    private lateinit var xmlFileService: XmlFileService

    @Test
    fun testProcessFile_GivenValidXmlFile_ReturnsFileOutput() {
        //arrange
        val file = buildXmlFile()
        xmlFileService = XmlFileService(buildXmlFileDefinition())

        //act
        val fileOutput = xmlFileService!!.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test
    fun testProcessFile_GivenFieldNotInFile_ThrowsFileRipperException() {
        //arrange
        val file = buildXmlFile()

        val fileDefinition = buildXmlFileDefinition()
        fileDefinition.fieldDefinitions
                .add(buildFieldDefinition("address", null, null))
        xmlFileService = XmlFileService(fileDefinition)

        //act
        assertThrows<FileRipperException> { xmlFileService.processFile(file) }
    }
}