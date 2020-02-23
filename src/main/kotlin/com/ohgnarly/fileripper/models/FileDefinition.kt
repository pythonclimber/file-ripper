package com.ohgnarly.fileripper.models


enum class FileType {
    DELIMITED,
    FIXED,
    XML
}

class FileDefinition {
    var fileMask: String = ""
    var fileType: FileType? = null
    var hasHeader: Boolean = false
    var delimiter: String = ""
    var fieldDefinitions: MutableList<FieldDefinition> = mutableListOf()
    var recordXmlElement: String = ""
    var inputDirectory: String = ""
    var completedDirectory: String = ""
}