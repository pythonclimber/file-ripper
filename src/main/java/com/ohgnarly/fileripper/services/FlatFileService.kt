package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.enums.FileType.DELIMITED
import com.ohgnarly.fileripper.enums.FileType.FIXED
import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import org.apache.commons.lang3.StringUtils.split
import java.io.File
import java.io.IOException
import java.lang.String.format
import java.nio.file.Files.readAllLines
import java.util.*

class FlatFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {

    @Throws(FileRipperException::class)
    override fun processFile(file: File): FileOutput {
        try {
            val fileOutput = FileOutput()
            fileOutput.fileName = file.name
            fileOutput.records = processLines(readAllLines(file.toPath()))
            return fileOutput
        } catch (ex: IOException) {
            throw FileRipperException("Error reading provided file", ex)
        }

    }

    @Throws(FileRipperException::class)
    private fun processLines(lines: MutableList<String>): List<Map<String, Any>> {
        val records = ArrayList<Map<String, Any>>()
        if (fileDefinition.hasHeader) { //if list has header, remove first line in list
            lines.removeAt(0)
        }

        for (line in lines) {
            if (fileDefinition.fileType === DELIMITED) {
                records.add(processDelimitedLine(line))
            } else if (fileDefinition.fileType === FIXED) {
                records.add(processFixedLine(line))
            }
        }
        return records
    }

    @Throws(FileRipperException::class)
    private fun processDelimitedLine(line: String): Map<String, Any> {
        val fields = split(line, fileDefinition.delimiter)
        if (fields.size < fileDefinition.fieldDefinitions.size) {
            throw FileRipperException(format("Record '%s' has invalid number of fields", line))
        }

        val record = LinkedHashMap<String, Any>()
        for (i in fields.indices) {
            val fieldName = fileDefinition.fieldDefinitions[i].fieldName
            val fieldValue = fields[i]


            record[fieldName] = fieldValue
        }
        return record
    }

    @Throws(FileRipperException::class)
    private fun processFixedLine(line: String): Map<String, Any> {
        val record = LinkedHashMap<String, Any>()
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
