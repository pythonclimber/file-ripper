package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Element
import org.xml.sax.SAXParseException
import java.io.File
import java.nio.file.Files.readAllLines
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

abstract class FileService protected constructor(protected var fileDefinition: FileDefinition) {
    @Throws(FileRipperException::class)
    abstract fun processFile(file: File): FileOutput

    abstract fun isDefinitionValid(): ValidationResult

    companion object : (FileDefinition) -> FileService {
        override fun invoke(fileDefinition: FileDefinition): FileService {
            return when (fileDefinition.fileType) {
                FileType.DELIMITED -> DelimitedFileService(fileDefinition)
                FileType.FIXED -> FixedFileService(fileDefinition)
                FileType.XML -> XmlFileService(fileDefinition)
                else -> throw IllegalArgumentException("Invalid file type provided")
            }
        }

        fun create(fileDefinition: FileDefinition): FileService {
            return invoke(fileDefinition)
        }
    }
}

abstract class FlatFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {
    override fun processFile(file: File): FileOutput {
        val validationResult = isDefinitionValid()
        if (!validationResult.isValid()) {
            throw FileRipperException("File definition is missing the following fields: ${validationResult.getValidationMessage()}")
        }

        return FileOutput().apply {
            fileName = file.name
            records = processLines(readAllLines(file.toPath()))
        }
    }

    private fun processLines(lines: MutableList<String>): List<Map<String, String>> {
        val records = mutableListOf<Map<String, String>>()
        if (fileDefinition.hasHeader) { //if list has header, remove first line in list
            lines.removeAt(0)
        }

        for (line in lines) {
            records.add(processLine(line))
        }

        return records
    }

    protected abstract fun processLine(line: String): Map<String, String>
}

class XmlFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {
    override fun processFile(file: File): FileOutput {
        val fileOutput = FileOutput()
        fileOutput.fileName = file.name
        val records = ArrayList<Map<String, String>>()

        try {
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = documentBuilder.parse(file)
            val root = doc.documentElement
            val nodes = root.getElementsByTagName(fileDefinition.recordXmlElement)
            for (i in 0 until nodes.length) {
                val person = nodes.item(i) as Element
                val record = LinkedHashMap<String, String>()
                for (fieldDefinition in fileDefinition.fieldDefinitions) {
                    val tagName: String = fieldDefinition.xmlFieldName ?: fieldDefinition.fieldName
                    val fieldNodes = person.getElementsByTagName(tagName)
                    if (fieldNodes.length == 0) {
                        throw FileRipperException(java.lang.String.format("Field %s is does not exist in file",
                                fieldDefinition.fieldName))
                    }

                    val fieldName = fieldDefinition.fieldName
                    val fieldValue = fieldNodes.item(0).textContent
                    record[fieldName] = fieldValue
                }
                records.add(record)
            }
            fileOutput.records = records
        } catch (ex: SAXParseException) {
            throw FileRipperException("Input file is not valid XML", ex)
        }
        return fileOutput
    }

    override fun isDefinitionValid(): ValidationResult {
        val missingFields = mutableListOf<String>()

        if (fileDefinition.delimiter.isBlank()) {
            missingFields.add("delimiter")
        }

        return ValidationResult(mutableListOf())
    }
}

class DelimitedFileService(fileDefinition: FileDefinition) : FlatFileService(fileDefinition) {
    override fun processLine(line: String): Map<String, String> {
        val fields = StringUtils.split(line, fileDefinition.delimiter)
        if (fields.size < fileDefinition.fieldDefinitions.size) {
            throw FileRipperException("Record '${line}' has invalid number of fields")
        }

        val record = LinkedHashMap<String, String>()
        for (fieldDefinition in fileDefinition.fieldDefinitions) {
            val fieldName = fieldDefinition.fieldName
            val fieldValue = fields[fieldDefinition.positionInRow!!]
            record[fieldName] = fieldValue
        }
        return record
    }

    override fun isDefinitionValid(): ValidationResult {
        val missingFields = mutableListOf<String>()

        if (fileDefinition.delimiter.isBlank()) {
            missingFields.add("delimiter")
        }

        return ValidationResult(missingFields)
    }
}

class FixedFileService(fileDefinition: FileDefinition) : FlatFileService(fileDefinition) {
    override fun processLine(line: String): Map<String, String> {
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

    override fun isDefinitionValid(): ValidationResult {
        return ValidationResult(mutableListOf())
    }
}