package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.testhelpers.buildFieldDefinition
import com.ohgnarly.fileripper.testhelpers.buildXmlFile
import com.ohgnarly.fileripper.testhelpers.buildXmlFileDefinition
import org.junit.Test
import kotlin.test.assertEquals

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
    }

    @Test(expected = FileRipperException::class)
    @Throws(Throwable::class)
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