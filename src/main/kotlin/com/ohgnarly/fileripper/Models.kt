package com.ohgnarly.fileripper

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
    var completedDirectory: String? = null
}

class FieldDefinition {

    var fieldName: String = ""
    var startPosition: Int? = null
    var fieldLength: Int? = null
}

class FileOutput {
    var fileName: String = ""
    var records: List<Map<String, String>> = mutableListOf()
}

class FileResult<T>(var fileName: String) {

    var records: MutableList<T> = mutableListOf()
}

class FileRipperException : Exception {
    constructor(message: String) : super(message) {}

    constructor(cause: Throwable) : super(cause) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}