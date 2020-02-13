package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import org.w3c.dom.Element
import java.io.File
import java.lang.String.format
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class XmlFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {

    override fun processFile(file: File): FileOutput {
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
    }
}
