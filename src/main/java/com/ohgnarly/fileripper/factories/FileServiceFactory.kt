package com.ohgnarly.fileripper.factories

import com.ohgnarly.fileripper.enums.FileType
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.services.FileService
import com.ohgnarly.fileripper.services.FlatFileService
import com.ohgnarly.fileripper.services.XmlFileService
import org.springframework.stereotype.Component

interface FileServiceFactory {
    fun createFileService(fileDefinition: FileDefinition): FileService
}

@Component
class DefaultFileServiceFactory : FileServiceFactory {
    override fun createFileService(fileDefinition: FileDefinition): FileService {
        return when (fileDefinition.fileType) {
            FileType.DELIMITED, FileType.FIXED -> FlatFileService(fileDefinition)
            FileType.XML -> XmlFileService(fileDefinition)
            else -> throw IllegalArgumentException("Invalid file type provided")
        }
    }
}