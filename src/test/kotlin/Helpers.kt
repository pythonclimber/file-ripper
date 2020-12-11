package com.ohgnarly.fileripper

import FieldDefinition
import FileDefinition
import FileType
import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Assert.*
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals

class Person(var name: String, var age: String, var dob: String)

fun buildPerson(fields: Map<String, String>): Person {
    return Person(fields.getValue("name"), fields.getValue("age"), fields.getValue("dob"))
}

fun buildDelimitedFileDefinition(delimiter: String): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>().apply {
        add(buildFieldDefinition("name", null, null, 0))
        add(buildFieldDefinition("age", null, null, 1))
        add(buildFieldDefinition("dob", null, null, 2))
    }

    return FileDefinition().apply {
        fileMask = "Valid-Delimited-*.txt"
        fileType = FileType.DELIMITED
        this.delimiter = delimiter
        hasHeader = false
        this.fieldDefinitions = fieldDefinitions
        inputDirectory = "/path"
    }
}

fun buildXmlFileDefinition(): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>().apply {
        add(buildFieldDefinition("name", null, null, null))
        add(buildFieldDefinition("age", null, null, null))
        add(buildFieldDefinition("dob", null, null, null))
    }

    val fileDefinition = FileDefinition().apply {
        fileType = FileType.XML
        fileMask = "Valid-Xml-*.txt"
        hasHeader = false
        recordXmlElement = "Person"
        this.fieldDefinitions = fieldDefinitions
        inputDirectory = "/path"
    }

    return fileDefinition
}

fun buildFixedFileDefinition(): FileDefinition {
    val fieldDefinitions = mutableListOf<FieldDefinition>().apply {
        add(buildFieldDefinition("name", 0, 20, null))
        add(buildFieldDefinition("age", 20, 5, null))
        add(buildFieldDefinition("dob", 25, 10, null))
    }


    return FileDefinition().apply {
        fileMask = "Valid-Fixed-*.txt"
        fileType = FileType.FIXED
        hasHeader = true
        this.fieldDefinitions = fieldDefinitions
        inputDirectory = "/path"
    }
}

fun buildFieldDefinition(fieldName: String, startPosition: Int?, fieldLength: Int?, positionInRow: Int?): FieldDefinition {
    return FieldDefinition().apply {
        this.fieldName = fieldName
        this.startPosition = startPosition
        this.fieldLength = fieldLength
        this.positionInRow = positionInRow
    }
}

fun buildDelimitedFile(delimiter: String, hasHeader: Boolean): File {
    val lines = mutableListOf<String>().apply {
        if (hasHeader) {
            add(StringUtils.join(listOf("name", "age", "dob"), delimiter))
        }

        add(StringUtils.join(listOf("Aaron", "39", "09/04/1980"), delimiter))
        add(StringUtils.join(listOf("Gene", "61", "01/15/1958"), delimiter))
        add(StringUtils.join(listOf("Alexander", "4", "11/22/2014"), delimiter))
        add(StringUtils.join(listOf("Mason", "12", "04/13/2007"), delimiter))
    }

    return writeFile("Valid-Delimited-", ".txt", lines)
}

fun buildFixedFile(hasHeader: Boolean): File {
    val lines = mutableListOf<String>().apply {
        if (hasHeader) {
            add("${StringUtils.rightPad("name", 20)}${StringUtils.rightPad("age", 5)}${StringUtils.rightPad("dob", 10)}")
        }

        add("${StringUtils.rightPad("Aaron", 20)}${StringUtils.rightPad("39", 5)}09/04/1980")
        add("${StringUtils.rightPad("Gene", 20)}${StringUtils.rightPad("61", 5)}01/15/1958")
        add("${StringUtils.rightPad("Alexander", 20)}${StringUtils.rightPad("4", 5)}11/22/2014")
        add("${StringUtils.rightPad("Mason", 20)}${StringUtils.rightPad("12", 5)}04/13/2007")
    }

    return writeFile("Valid-Fixed-", ".txt", lines)
}

fun buildXmlFile(): File {
    val lines = mutableListOf<String>().apply {
        add("<People>")
        addAll(buildXmlRecord(listOf("Aaron", "39", "09/04/1980")))
        addAll(buildXmlRecord(listOf("Gene", "61", "01/15/1958")))
        addAll(buildXmlRecord(listOf("Alexander", "4", "11/22/2014")))
        addAll(buildXmlRecord(listOf("Mason", "12", "04/13/2007")))
        add("</People>")
    }

    return writeFile("Valid-Xml-", ".xml", lines)
}

fun assertFileRecords(records: List<Map<String, Any>>) {
    assertFileRecord(records[0], "Aaron", "39", "09/04/1980")
    assertFileRecord(records[1], "Gene", "61", "01/15/1958")
    assertFileRecord(records[2], "Alexander", "4", "11/22/2014")
    assertFileRecord(records[3], "Mason", "12", "04/13/2007")
}

fun assertFileRecord(record: Map<String, Any>, name: String, age: String, dob: String) {
    assertEquals(name, record["name"])
    assertEquals(age, record["age"])
    assertEquals(dob, record["dob"])
}

fun assertPeople(people: List<Person>) {
    assertPerson(people[0], "Aaron", "39", "09/04/1980")
    assertPerson(people[1], "Gene", "61", "01/15/1958")
    assertPerson(people[2], "Alexander", "4", "11/22/2014")
    assertPerson(people[3], "Mason", "12", "04/13/2007")
}

fun assertPerson(person: Person, name: String, age: String, dob: String) {
    assertEquals(name, person.name)
    assertEquals(age, person.age)
    assertEquals(dob, person.dob)
}

fun writeFile(prefix: String, suffix: String, lines: List<String>): File {
    return Files.write(createTempFile(prefix, suffix).toPath(), lines).toFile()
}

private fun buildXmlRecord(fields: List<String>): List<String> {
    return mutableListOf<String>().apply {
        add("\t<Person>")
        add("\t\t<name>${fields[0]}</name>")
        add("\t\t<age>${fields[1]}</age>")
        add("\t\t<dob>${fields[2]}</dob>")
        add("\t</Person>")
    }
}
