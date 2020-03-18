package com.ohgnarly.fileripper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.commons.lang3.StringUtils.join
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertFailsWith

class FileRipperTest {
    private val fileServiceFactory = { _: FileDefinition -> mockFileService }
    private val mockFileService = mockk<FileService>()
    private val mockFileRepository = mockk<FileRepository>()
    private val mockFileMover = mockk<FileMover>(relaxed = true)
    private val fileRipper = FileRipper(mockFileRepository, fileServiceFactory, mockFileMover)

    @Test
    fun testRipFile_GivenFileAndFileDefinition_ReturnsFileOutput() {
        //arrange
        println("Junit Version: ${junit.runner.Version.id()}")
        val file = Files.createTempFile("temp", ".temp").toFile()
        val expectedOutput = FileOutput()
        val fileDefinition = FileDefinition()

        every {
            mockFileService.processFile(file)
        } returns expectedOutput

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

        every { mockFileService.processFile(file) } throws FileRipperException("Yay!")

        //act
        val exception = assertFailsWith<FileRipperException> {fileRipper.ripFile(file, fileDefinition)}

        //arrange
        assertEquals("Yay!", exception.message)
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

        every { mockFileService.processFile(file) } returns fileOutput

        val builder = { fields: Map<String, Any> -> join(fields.values, " ") }

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
        val fileDefinition = FileDefinition().apply {
            completedDirectory = "/completed/"
        }

        every { mockFileService.processFile(file) } returns expectedOutput

        //act
        val fileOutputs = fileRipper.ripFiles(mutableListOf(file), fileDefinition)

        //assert
        assertEquals(1, fileOutputs.size)
        assertEquals(expectedOutput, fileOutputs[0])
        verify(exactly = 1) { mockFileMover.moveFiles(any(), any()) }
    }

    @Test
    fun testRipFiles_GivenFileListAndFileDefinitionAndObjectBuilder_ReturnsFileResult() {
        //arrange
        val file = createTempFile("temp", ".temp")
        val fileDefinition = FileDefinition().apply {
            completedDirectory = "/completed/"
        }

        val fieldMap = mutableMapOf<String, String>()
        fieldMap["1"] = "Aaron"
        fieldMap["2"] = "Smith"

        val fileOutput = FileOutput()
        fileOutput.fileName = "Hello.txt"
        fileOutput.records = mutableListOf(fieldMap)

        every { mockFileService.processFile(file) } returns fileOutput

        val builder = { fields: Map<String, Any> -> join(fields.values, " ") }

        //act
        val fileResults = fileRipper.ripFiles(mutableListOf(file), fileDefinition, builder)

        //arrange
        assertEquals(1, fileResults.size)
        assertEquals("Hello.txt", fileResults[0].fileName)
        assertEquals(1, fileResults[0].records.size)
        assertEquals("Aaron Smith", fileResults[0].records[0])
        verify(exactly = 1) { mockFileMover.moveFiles(any(), any()) }
    }

    @Test
    fun testFindAndRipFiles_GivenFileDefinition_ReturnsListOfFileOutput() {
        //arrange
        val file = Files.createTempFile("temp", ".temp").toFile()
        val expectedOutput = FileOutput()
        val fileDefinition = FileDefinition()

        every { mockFileRepository.getFiles(allAny(), allAny()) } returns mutableListOf(file)
        every { mockFileService.processFile(file) } returns expectedOutput

        //act
        val fileOutputs = fileRipper.findAndRipFiles(fileDefinition)

        //assert
        assertEquals(1, fileOutputs.size)
        assertEquals(expectedOutput, fileOutputs[0])
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

        every { mockFileRepository.getFiles(allAny(), allAny()) } returns mutableListOf(file)
        every { mockFileService.processFile(file) } returns fileOutput

        val builder = { fields: Map<String, Any> -> join(fields.values, " ") }

        //act
        val fileResults = fileRipper.findAndRipFiles(fileDefinition, builder)

        //arrange
        assertEquals(1, fileResults.size)
        assertEquals("Hello.txt", fileResults[0].fileName)
        assertEquals(1, fileResults[0].records.size)
        assertEquals("Aaron Smith", fileResults[0].records[0])
    }
}