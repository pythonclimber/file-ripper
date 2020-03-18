package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Element
import java.io.File
import java.nio.file.Files
import java.util.ArrayList
import java.util.LinkedHashMap
import javax.xml.parsers.DocumentBuilderFactory

abstract class FileService protected constructor(protected var fileDefinition: FileDefinition) {
    @Throws(FileRipperException::class)
    abstract fun processFile(file: File): FileOutput

    companion object : (FileDefinition) -> FileService {
        override fun invoke(fileDefinition: FileDefinition): FileService {
            return when (fileDefinition.fileType) {
                FileType.DELIMITED, FileType.FIXED -> FlatFileService(fileDefinition)
                FileType.XML -> XmlFileService(fileDefinition)
                else -> throw IllegalArgumentException("Invalid file type provided")
            }
        }

        fun create(fileDefinition: FileDefinition): FileService {
            return invoke(fileDefinition)
        }
    }
}

class FlatFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {
    override fun processFile(file: File): FileOutput {
        val fileOutput = FileOutput()
        fileOutput.fileName = file.name
        fileOutput.records = processLines(Files.readAllLines(file.toPath()))
        return fileOutput
    }

    private fun processLines(lines: MutableList<String>): List<Map<String, String>> {
        val records = ArrayList<Map<String, String>>()
        if (fileDefinition.hasHeader) { //if list has header, remove first line in list
            lines.removeAt(0)
        }

        for (line in lines) {
            if (fileDefinition.fileType === FileType.DELIMITED) {
                records.add(processDelimitedLine(line))
            } else if (fileDefinition.fileType === FileType.FIXED) {
                records.add(processFixedLine(line))
            }
        }
        return records
    }

    private fun processDelimitedLine(line: String): Map<String, String> {
        val fields = StringUtils.split(line, fileDefinition.delimiter)
        if (fields.size < fileDefinition.fieldDefinitions.size) {
            throw FileRipperException(java.lang.String.format("Record '%s' has invalid number of fields", line))
        }

        val record = LinkedHashMap<String, String>()
        for (i in fields.indices) {
            val fieldName = fileDefinition.fieldDefinitions[i].fieldName
            val fieldValue = fields[i]


            record[fieldName] = fieldValue
        }
        return record
    }

    private fun processFixedLine(line: String): Map<String, String> {
        val record = LinkedHashMap<String, String>()
        for (fieldDefinition in fileDefinition.fieldDefinitions) {
            val startPosition = fieldDefinition.startPosition!!
            val endPosition = fieldDefinition.startPosition!! + fieldDefinition.fieldLength!!
            if (startPosition > line.length || endPosition > line.length) {
                throw FileRipperException("Invalid line length in fixed width file.")
            }

            val fieldValue = line.substring(fieldDefinition.startPosition!!, endPosition).trim { it <= ' ' }
            record[fieldDefinition.fieldName] = fieldValue
        }
        return record
    }
}

class XmlFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {
    override fun processFile(file: File): FileOutput {
        val fileOutput = FileOutput()
        fileOutput.fileName = file.name
        val records = ArrayList<Map<String, String>>()

        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = documentBuilder.parse(file)
        val root = doc.documentElement
        val nodes = root.getElementsByTagName(fileDefinition.recordXmlElement)
        for (i in 0 until nodes.length) {
            val person = nodes.item(i) as Element
            val record = LinkedHashMap<String, String>()
            for (fieldDefinition in fileDefinition.fieldDefinitions) {
                val fieldNodes = person.getElementsByTagName(fieldDefinition.fieldName)
                if (fieldNodes.length == 0) {
                    throw FileRipperException(java.lang.String.format("Field %s is does not exist in file",
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