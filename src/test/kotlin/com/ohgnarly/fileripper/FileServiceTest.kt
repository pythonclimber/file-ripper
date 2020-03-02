package com.ohgnarly.fileripper

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertEquals

class FileServiceCompanionTest {
    @Rule
    @JvmField
    var expectedException: ExpectedException = ExpectedException.none()

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

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Invalid file type provided")

        //act
        FileService.create(fileDefinition)
    }
}

class FlatFileServiceTest {
    private var flatFileService: FlatFileService? = null

    @Test
    fun testProcessFile_GivenDelimitedFile_ReturnsFileOutput() {
        //arrange
        val file = buildDelimitedFile("|", false)
        flatFileService = FlatFileService(buildDelimitedFileDefinition("|"))

        //act
        val fileOutput = flatFileService!!.processFile(file)

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
        val fileOutput = flatFileService!!.processFile(file)

        //assert
        assertEquals(file.name, fileOutput.fileName)
        assertFileRecords(fileOutput.records)
    }

    @Test(expected = FileRipperException::class)
    fun testProcessFile_GivenDelimitedFile_AndInvalidFileFormat_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition("|")
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", null, null))
        flatFileService = FlatFileService(fileDefinition)

        val file = buildDelimitedFile("|", false)

        //act
        flatFileService!!.processFile(file)
    }

    @Test(expected = FileRipperException::class)
    fun testProcessFile_GivenFixedFile_AndLastFieldTooLong_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions[2].fieldLength = 11
        flatFileService = FlatFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act
        flatFileService!!.processFile(file)
    }

    @Test(expected = FileRipperException::class)
    fun testProcessFile_GivenTooManyFields_ThrowsFileRipperException() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()
        fileDefinition.fieldDefinitions.add(buildFieldDefinition("address", 36, 0))
        flatFileService = FlatFileService(fileDefinition)

        val file = buildFixedFile(true)

        //act
        flatFileService!!.processFile(file)
    }
}

class XmlFileServiceTest {
    private var xmlFileService: XmlFileService? = null

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

    @Test(expected = FileRipperException::class)
    fun testProcessFile_GivenFieldNotInFile_ThrowsFileRipperException() {
        //arrange
        val file = buildXmlFile()

        val fileDefinition = buildXmlFileDefinition()
        fileDefinition.fieldDefinitions
                .add(buildFieldDefinition("address", null, null))
        xmlFileService = XmlFileService(fileDefinition)

        //act
        xmlFileService!!.processFile(file)
    }
}