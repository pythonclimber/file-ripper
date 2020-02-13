package com.ohgnarly.fileripper.testhelpers

import org.apache.commons.lang3.StringUtils.join
import org.apache.commons.lang3.StringUtils.rightPad
import java.io.File
import java.nio.file.Files.write

fun buildDelimitedFile(delimiter: String, hasHeader: Boolean): File {
    val lines = mutableListOf<String>()

    if (hasHeader) {
        lines.add(join(listOf("name", "age", "dob"), delimiter))
    }

    lines.add(join(listOf("Aaron", "39", "09/04/1980"), delimiter))
    lines.add(join(listOf("Gene", "61", "01/15/1958"), delimiter))
    lines.add(join(listOf("Alexander", "4", "11/22/2014"), delimiter))
    lines.add(join(listOf("Mason", "12", "04/13/2007"), delimiter))

    val file = createTempFile("Valid-Delimited-", ".txt")
    return write(file.toPath(), lines).toFile()
}

fun buildFixedFile(hasHeader: Boolean): File {
    val lines = mutableListOf<String>()

    if (hasHeader) {
        lines.add("${rightPad("name", 20)}${rightPad("age", 5)}${rightPad("dob", 10)}")
    }

    lines.add("${rightPad("Aaron", 20)}${rightPad("39", 5)}09/04/1980")
    lines.add("${rightPad("Gene", 20)}${rightPad("61", 5)}01/15/1958")
    lines.add("${rightPad("Alexander", 20)}${rightPad("4", 5)}11/22/2014")
    lines.add("${rightPad("Mason", 20)}${rightPad("12", 5)}04/13/2007")

    val path = createTempFile("Valid-Fixed-", ".txt").toPath()
    return write(path, lines).toFile()
}

fun buildXmlFile(): File {
    val lines = mutableListOf<String>()

    lines.add("<People>");
    lines.addAll(buildXmlRecord(listOf("Aaron", "39", "09/04/1980")));
    lines.addAll(buildXmlRecord(listOf("Gene", "61", "01/15/1958")));
    lines.addAll(buildXmlRecord(listOf("Alexander", "4", "11/22/2014")));
    lines.addAll(buildXmlRecord(listOf("Mason", "12", "04/13/2007")));
    lines.add("</People>");

    val path = createTempFile("Valid-Xml-", ".xml").toPath()
    return write(path, join(lines, "").toByteArray()).toFile();
}

private fun buildXmlRecord(fields: List<String>): List<String> {
    val lines = mutableListOf<String>()
    lines.add("\t<Person>");
    lines.add("\t\t<name>${fields[0]}</name>")
    lines.add("\t\t<age>${fields[1]}</age>")
    lines.add("\t\t<dob>${fields[2]}</dob>")
    lines.add("\t</Person>")
    return lines
}

