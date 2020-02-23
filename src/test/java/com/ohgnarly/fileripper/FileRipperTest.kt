package com.ohgnarly.fileripper

import com.nhaarman.mockito_kotlin.*
import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.repositories.FileRepository
import com.ohgnarly.fileripper.movers.FileMover
import com.ohgnarly.fileripper.services.FileService
import org.apache.commons.lang3.StringUtils.join
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.nio.file.Files

class FileRipperTest {
    private val fileServiceFactory = {_: FileDefinition -> mockFileService}
    private val mockFileService = mock<FileService>()
    private val mockFileRepository = mock<FileRepository>()
    private val mockFileMover = mock<FileMover>()
    private val fileRipper = FileRipper(mockFileRepository, fileServiceFactory, mockFileMover)

    @Rule
    @JvmField
    var expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun testRipFile_GivenFileAndFileDefinition_ReturnsFileOutput() {
        //arrange
        val file = Files.createTempFile("temp", ".temp").toFile()
        val expectedOutput = FileOutput()
        val fileDefinition = FileDefinition()

        whenever(mockFileService.processFile(file)).thenReturn(expectedOutput)

        //act
        val fileOutput = fileRipper.ripFile(file, fileDefinition)

        //assert
        assertEquals(expectedOutput, fileOutput)
    }

    @Test
    fun testRipFile_GivenFileServiceThrowsException_ThrowsException() {
        //arrange
        val file = Files.createTempFile("temp", ".temp").toFile()
        val fileDefinition = FileDefinition()

        whenever(mockFileService.processFile(file)).thenThrow(FileRipperException("Yay!"))

        expectedException.expect(FileRipperException::class.java)
        expectedException.expectMessage("Yay!")

        //act
        fileRipper.ripFile(file, fileDefinition)
    }

    @Test
    fun testRipFile_GivenFileAndFileDefinitionAndObjectBuilder_ReturnsFileResult() {
        //arrange
        val file = createTempFile("temp", ".temp")
        val fileDefinition = FileDefinition()

        val fieldMap = mutableMapOf<String, String>()
        fieldMap["1"] = "Aaron"
        fieldMap["2"] = "Smith"

        val fileOutput = FileOutput()
        fileOutput.fileName = "Hello.txt"
        fileOutput.records = mutableListOf(fieldMap)

        whenever(mockFileService.processFile(file)).thenReturn(fileOutput)

        val builder = {fields: Map<String, Any> -> join(fields.values, " ") }

        //act
        val fileResult = fileRipper.ripFile(file, fileDefinition, builder)

        //arrange
        assertEquals("Hello.txt", fileResult.fileName)
        assertEquals(1, fileResult.records.size)
        assertEquals("Aaron Smith", fileResult.records[0])
    }

    @Test
    fun testRipFiles_GivenFileListAndFileDefinition_ReturnsListOfFileOutput() {
        //arrange
        val file = Files.createTempFile("temp", ".temp").toFile()
        val expectedOutput = FileOutput()
        val fileDefinition = FileDefinition()

        whenever(mockFileService.processFile(file)).thenReturn(expectedOutput)

        //act
        val fileOutputs = fileRipper.ripFiles(mutableListOf(file), fileDefinition)

        //assert
        assertEquals(1, fileOutputs.size)
        assertEquals(expectedOutput, fileOutputs[0])
    }

    @Test
    fun testRipFiles_GivenFileListAndFileDefinitionAndObjectBuilder_ReturnsFileResult() {
        //arrange
        val file = createTempFile("temp", ".temp")
        val fileDefinition = FileDefinition()

        val fieldMap = mutableMapOf<String, String>()
        fieldMap["1"] = "Aaron"
        fieldMap["2"] = "Smith"

        val fileOutput = FileOutput()
        fileOutput.fileName = "Hello.txt"
        fileOutput.records = mutableListOf(fieldMap)

        whenever(mockFileService.processFile(file)).thenReturn(fileOutput)

        val builder = {fields: Map<String, Any> -> join(fields.values, " ") }

        //act
        val fileResults = fileRipper.ripFiles(mutableListOf(file), fileDefinition, builder)

        //arrange
        assertEquals(1, fileResults.size)
        assertEquals("Hello.txt", fileResults[0].fileName)
        assertEquals(1, fileResults[0].records.size)
        assertEquals("Aaron Smith", fileResults[0].records[0])
    }

    @Test
    fun testFindAndRipFiles_GivenFileDefinition_ReturnsListOfFileOutput() {
        //arrange
        val file = Files.createTempFile("temp", ".temp").toFile()
        val expectedOutput = FileOutput()
        val fileDefinition = FileDefinition()

        whenever(mockFileRepository.getFiles(any(), any())).thenReturn(mutableListOf(file))
        whenever(mockFileService.processFile(file)).thenReturn(expectedOutput)

        //act
        val fileOutputs = fileRipper.findAndRipFiles(fileDefinition)

        //assert
        assertEquals(1, fileOutputs.size)
        assertEquals(expectedOutput, fileOutputs[0])
        verify(mockFileMover, times(1)).moveFiles(any(), any())
    }

    @Test
    fun testFindAndRipFiles_GivenFileDefinitionAndObjectBuilder_ReturnsFileResult() {
        //arrange
        val file = createTempFile("temp", ".temp")
        val fileDefinition = FileDefinition()

        val fieldMap = mutableMapOf<String, String>()
        fieldMap["1"] = "Aaron"
        fieldMap["2"] = "Smith"

        val fileOutput = FileOutput()
        fileOutput.fileName = "Hello.txt"
        fileOutput.records = mutableListOf(fieldMap)

        whenever(mockFileRepository.getFiles(any(), any())).thenReturn(mutableListOf(file))
        whenever(mockFileService.processFile(file)).thenReturn(fileOutput)

        val builder = {fields: Map<String, Any> -> join(fields.values, " ") }

        //act
        val fileResults = fileRipper.findAndRipFiles(fileDefinition, builder)

        //arrange
        assertEquals(1, fileResults.size)
        assertEquals("Hello.txt", fileResults[0].fileName)
        assertEquals(1, fileResults[0].records.size)
        assertEquals("Aaron Smith", fileResults[0].records[0])
        verify(mockFileMover, times(1)).moveFiles(any(), any())
    }
}