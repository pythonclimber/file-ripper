package com.ohgnarly.fileripper

import com.ohgnarly.fileripper.factories.createFileService
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.services.FileService
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileRipper(private val serviceFactory: (FileDefinition) -> FileService) {
    constructor() : this(::createFileService)

    fun ripFile(file: File, fileDefinition: FileDefinition): FileOutput {
        val fileService = serviceFactory(fileDefinition)
        return fileService.processFile(file)
    }
}