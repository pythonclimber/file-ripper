package com.ohgnarly.fileripper.factories

import com.ohgnarly.fileripper.services.FlatFileService
import com.ohgnarly.fileripper.services.XmlFileService
import com.ohgnarly.fileripper.testhelpers.buildDelimitedFileDefinition
import com.ohgnarly.fileripper.testhelpers.buildFixedFileDefinition
import com.ohgnarly.fileripper.testhelpers.buildXmlFileDefinition
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FileServiceFactoryTest {
    private var fileServiceFactory: FileServiceFactory? = null

    @Before
    fun setUp() {
        fileServiceFactory = DefaultFileServiceFactory()
    }

    @Test
    fun testCreateFileService_GivenDelimitedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildDelimitedFileDefinition(",")

        //act
        val fileService = fileServiceFactory!!.createFileService(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenFixedFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildFixedFileDefinition()

        //act
        val fileService = fileServiceFactory!!.createFileService(fileDefinition)

        //assert
        Assert.assertTrue(fileService is FlatFileService)
    }

    @Test
    fun testCreateFileService_GivenXmlFileDefinition_ShouldReturnDelimitedFileService() {
        //arrange
        val fileDefinition = buildXmlFileDefinition()

        //act
        val fileService = fileServiceFactory!!.createFileService(fileDefinition)

        //assert
        Assert.assertTrue(fileService is XmlFileService)
    }
}