package com.ohgnarly.fileripper.controllers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.ohgnarly.fileripper.factories.FileServiceFactory
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.models.FileRipperRequest
import com.ohgnarly.fileripper.services.FileService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.HttpStatus.OK
import org.springframework.mock.web.MockMultipartFile

@RunWith(MockitoJUnitRunner::class)
class FileRipperControllerTest {
    private val mockFileService = mock<FileService>()
    private val mockFileServiceFactory = mock<FileServiceFactory>()
    private var fileRipperController = FileRipperController(mockFileServiceFactory)

    @Test
    @Throws(Throwable::class)
    fun testGetFileOutput_GivenEmptyFile_ShouldReceiveGenericResponse() {
        //arrange
        val file = MockMultipartFile("Hello-World.txt", null as ByteArray?)
        val fileDefinition = FileDefinition()
        val request = FileRipperRequest()

        request.fileDefinition = fileDefinition
        request.multipartFile = file

        whenever(mockFileService!!.processFile(any())).thenReturn(createExceptedOutput(file.name))
        whenever(mockFileServiceFactory!!.createFileService(fileDefinition)).thenReturn(mockFileService)

        //act
        val response = fileRipperController!!.getFileOutput(request)

        //assert
        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(file.name, response.body!!.fileName)
    }

    private fun createExceptedOutput(fileName: String): FileOutput {
        val fileOutput = FileOutput()
        fileOutput.fileName = fileName
        fileOutput.records = mutableListOf()
        return fileOutput
    }
}