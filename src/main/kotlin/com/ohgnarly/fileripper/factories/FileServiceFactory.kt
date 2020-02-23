package com.ohgnarly.fileripper.factories

import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileType
import com.ohgnarly.fileripper.services.FileService
import com.ohgnarly.fileripper.services.FlatFileService
import com.ohgnarly.fileripper.services.XmlFileService


internal fun createFileService(fileDefinition: FileDefinition): FileService {
    return when (fileDefinition.fileType) {
        FileType.DELIMITED, FileType.FIXED -> FlatFileService(fileDefinition)
        FileType.XML -> XmlFileService(fileDefinition)
        else -> throw IllegalArgumentException("Invalid file type provided")
    }
}