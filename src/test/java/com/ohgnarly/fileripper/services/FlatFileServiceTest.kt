package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.testhelpers.*
import org.junit.Test
import kotlin.test.assertEquals

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