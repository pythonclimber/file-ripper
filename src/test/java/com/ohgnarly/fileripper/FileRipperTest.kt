package com.ohgnarly.fileripper

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.factories.FileServiceFactory
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.services.FileService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.nio.file.Files

class FileRipperTest {
    private val mockFileServiceFactory = mock<FileServiceFactory>()
    private val mockFileService = mock<FileService>()
    private val fileRipper = FileRipper(mockFileServiceFactory)

    @Rule
    @JvmField
    var expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        whenever(mockFileServiceFactory.createFileService(any())).thenReturn(mockFileService)
    }

    @Test
    fun testRipFile_GivenFile_AndFileDefinition_ReturnsFileOutput() {
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
}