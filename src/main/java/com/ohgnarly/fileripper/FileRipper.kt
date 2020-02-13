package com.ohgnarly.fileripper

import com.ohgnarly.fileripper.factories.DefaultFileServiceFactory
import com.ohgnarly.fileripper.factories.FileServiceFactory
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileRipper(private val fileServiceFactory: FileServiceFactory) {
    constructor() : this(DefaultFileServiceFactory())

    fun ripFile(file: File, fileDefinition: FileDefinition): FileOutput {
        val fileService = fileServiceFactory.createFileService(fileDefinition)
        return fileService.processFile(file)
    }
}