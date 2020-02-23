package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.models.FileType.DELIMITED
import com.ohgnarly.fileripper.models.FileType.FIXED
import org.apache.commons.lang3.StringUtils.split
import java.io.File
import java.lang.String.format
import java.nio.file.Files.readAllLines
import java.util.*

class FlatFileService(fileDefinition: FileDefinition) : FileService(fileDefinition) {
    override fun processFile(file: File): FileOutput {
        val fileOutput = FileOutput()
        fileOutput.fileName = file.name
        fileOutput.records = processLines(readAllLines(file.toPath()))
        return fileOutput
    }

    private fun processLines(lines: MutableList<String>): List<Map<String, String>> {
        val records = ArrayList<Map<String, String>>()
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

    private fun processDelimitedLine(line: String): Map<String, String> {
        val fields = split(line, fileDefinition.delimiter)
        if (fields.size < fileDefinition.fieldDefinitions.size) {
            throw FileRipperException(format("Record '%s' has invalid number of fields", line))
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
