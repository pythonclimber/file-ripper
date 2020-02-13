package com.ohgnarly.fileripper.testhelpers

import com.ohgnarly.fileripper.enums.FileType
import com.ohgnarly.fileripper.models.FieldDefinition
import com.ohgnarly.fileripper.models.FileDefinition

fun buildDelimitedFileDefinition(delimiter: String): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>()
    fieldDefinitions.add(buildFieldDefinition("name", null, null))
    fieldDefinitions.add(buildFieldDefinition("age", null, null))
    fieldDefinitions.add(buildFieldDefinition("dob", null, null))

    val fileDefinition = FileDefinition()
    fileDefinition.fileMask = "Valid-Delimited-*.txt"
    fileDefinition.fileType = FileType.DELIMITED
    fileDefinition.delimiter = delimiter
    fileDefinition.hasHeader = false
    fileDefinition.fieldDefinitions = fieldDefinitions

    return fileDefinition
}

fun buildXmlFileDefinition(): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>()
    fieldDefinitions.add(buildFieldDefinition("name", null, null))
    fieldDefinitions.add(buildFieldDefinition("age", null, null))
    fieldDefinitions.add(buildFieldDefinition("dob", null, null))

    val fileDefinition = FileDefinition()
    fileDefinition.fileType = FileType.XML
    fileDefinition.fileMask = "Valid-Xml-*.txt"
    fileDefinition.hasHeader = false
    fileDefinition.recordXmlElement = "Person"
    fileDefinition.fieldDefinitions = fieldDefinitions

    return fileDefinition
}

fun buildFixedFileDefinition(): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>()
    fieldDefinitions.add(buildFieldDefinition("name", 0, 20))
    fieldDefinitions.add(buildFieldDefinition("age", 20, 5))
    fieldDefinitions.add(buildFieldDefinition("dob", 25, 10))

    val fileDefinition = FileDefinition()
    fileDefinition.fileMask = "Valid-Fixed-*.txt"
    fileDefinition.fileType = FileType.FIXED
    fileDefinition.hasHeader = true
    fileDefinition.fieldDefinitions = fieldDefinitions

    return fileDefinition
}

fun buildFieldDefinition(fieldName: String, startPosition: Int?, fieldLength: Int?): FieldDefinition {
    val fieldDefinition = FieldDefinition()
    fieldDefinition.fieldName = fieldName
    fieldDefinition.startPosition = startPosition
    fieldDefinition.fieldLength = fieldLength
    return fieldDefinition
}