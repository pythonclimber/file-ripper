package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FieldDefinition
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.LinkedHashMap

import java.lang.String.format

class XmlFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {

    @Throws(FileRipperException::class)
    override fun processFile(file: File): FileOutput {
        try {
            val fileOutput = FileOutput()
            fileOutput.fileName = file.name
            val records = ArrayList<Map<String, Any>>()

            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = documentBuilder.parse(file)
            val root = doc.documentElement
            val nodes = root.getElementsByTagName(fileDefinition.recordXmlElement)
            for (i in 0 until nodes.length) {
                val person = nodes.item(i) as Element
                val record = LinkedHashMap<String, Any>()
                for (fieldDefinition in fileDefinition.fieldDefinitions) {
                    val fieldNodes = person.getElementsByTagName(fieldDefinition.fieldName)
                    if (fieldNodes.length == 0) {
                        throw FileRipperException(format("Field %s is does not exist in file",
                                fieldDefinition.fieldName))
                    }

                    val fieldName = fieldNodes.item(0).nodeName
                    val fieldValue = fieldNodes.item(0).textContent
                    record[fieldName] = fieldValue
                }
                records.add(record)
            }
            fileOutput.records = records
            return fileOutput
        } catch (ex: ParserConfigurationException) {
            throw FileRipperException(ex)
        } catch (ex: IOException) {
            throw FileRipperException(ex)
        } catch (ex: SAXException) {
            throw FileRipperException(ex)
        }

    }
}
