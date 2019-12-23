package com.ohgnarly.fileripper.models

import com.ohgnarly.fileripper.enums.FileType

class FileDefinition {
    var fileMask: String = ""
    var fileType: FileType? = null
    var hasHeader: Boolean = false
    var delimiter: String = ""
    var fieldDefinitions: List<FieldDefinition> = mutableListOf()
    var recordXmlElement: String? = ""
}