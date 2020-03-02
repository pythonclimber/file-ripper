package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals

class Person(var name: String, var age: String, var dob: String)

fun buildPerson(fields: Map<String, String>): Person {
    return Person(fields.getValue("name"), fields.getValue("age"), fields.getValue("dob"))
}

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
    fileDefinition.inputDirectory = "/path"

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
    fileDefinition.inputDirectory = "/path"

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
    fileDefinition.inputDirectory = "/path"

    return fileDefinition
}

fun buildFieldDefinition(fieldName: String, startPosition: Int?, fieldLength: Int?): FieldDefinition {
    val fieldDefinition = FieldDefinition()
    fieldDefinition.fieldName = fieldName
    fieldDefinition.startPosition = startPosition
    fieldDefinition.fieldLength = fieldLength
    return fieldDefinition
}

fun buildDelimitedFile(delimiter: String, hasHeader: Boolean): File {
    val lines = mutableListOf<String>()

    if (hasHeader) {
        lines.add(StringUtils.join(listOf("name", "age", "dob"), delimiter))
    }

    lines.add(StringUtils.join(listOf("Aaron", "39", "09/04/1980"), delimiter))
    lines.add(StringUtils.join(listOf("Gene", "61", "01/15/1958"), delimiter))
    lines.add(StringUtils.join(listOf("Alexander", "4", "11/22/2014"), delimiter))
    lines.add(StringUtils.join(listOf("Mason", "12", "04/13/2007"), delimiter))

    val file = createTempFile("Valid-Delimited-", ".txt")
    return Files.write(file.toPath(), lines).toFile()
}

fun buildFixedFile(hasHeader: Boolean): File {
    val lines = mutableListOf<String>()

    if (hasHeader) {
        lines.add("${StringUtils.rightPad("name", 20)}${StringUtils.rightPad("age", 5)}${StringUtils.rightPad("dob", 10)}")
    }

    lines.add("${StringUtils.rightPad("Aaron", 20)}${StringUtils.rightPad("39", 5)}09/04/1980")
    lines.add("${StringUtils.rightPad("Gene", 20)}${StringUtils.rightPad("61", 5)}01/15/1958")
    lines.add("${StringUtils.rightPad("Alexander", 20)}${StringUtils.rightPad("4", 5)}11/22/2014")
    lines.add("${StringUtils.rightPad("Mason", 20)}${StringUtils.rightPad("12", 5)}04/13/2007")

    val path = createTempFile("Valid-Fixed-", ".txt").toPath()
    return Files.write(path, lines).toFile()
}

fun buildXmlFile(): File {
    val lines = mutableListOf<String>()

    lines.add("<People>")
    lines.addAll(buildXmlRecord(listOf("Aaron", "39", "09/04/1980")))
    lines.addAll(buildXmlRecord(listOf("Gene", "61", "01/15/1958")))
    lines.addAll(buildXmlRecord(listOf("Alexander", "4", "11/22/2014")))
    lines.addAll(buildXmlRecord(listOf("Mason", "12", "04/13/2007")))
    lines.add("</People>")

    val path = createTempFile("Valid-Xml-", ".xml").toPath()
    return Files.write(path, StringUtils.join(lines, "").toByteArray()).toFile()
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
    Assert.assertEquals(name, person.name)
    Assert.assertEquals(age, person.age)
    Assert.assertEquals(dob, person.dob)
}

private fun buildXmlRecord(fields: List<String>): List<String> {
    val lines = mutableListOf<String>()
    lines.add("\t<Person>")
    lines.add("\t\t<name>${fields[0]}</name>")
    lines.add("\t\t<age>${fields[1]}</age>")
    lines.add("\t\t<dob>${fields[2]}</dob>")
    lines.add("\t</Person>")
    return lines
}