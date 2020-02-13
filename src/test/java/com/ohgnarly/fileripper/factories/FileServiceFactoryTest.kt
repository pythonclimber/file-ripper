package com.ohgnarly.fileripper.factories

import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.services.FlatFileService
import com.ohgnarly.fileripper.services.XmlFileService
import com.ohgnarly.fileripper.testhelpers.buildDelimitedFileDefinition
import com.ohgnarly.fileripper.testhelpers.buildFixedFileDefinition
import com.ohgnarly.fileripper.testhelpers.buildXmlFileDefinition
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.IllegalArgumentException

class FileServiceFactoryTest {
    @Rule
    @JvmField
    var expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun testCreateFileService_GivenDelimitedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition(",")

        //act
        val fileService = createFileService(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenFixedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()

        //act
        val fileService = createFileService(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenXmlFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildXmlFileDefinition()

        //act
        val fileService = createFileService(fileDefinition)

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
        createFileService(fileDefinition)
    }
}