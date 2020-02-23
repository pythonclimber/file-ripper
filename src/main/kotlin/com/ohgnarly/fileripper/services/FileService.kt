package com.ohgnarly.fileripper.services

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import java.io.File

abstract class FileService protected constructor(internal var fileDefinition: FileDefinition) {
    @Throws(FileRipperException::class)
    abstract fun processFile(file: File): FileOutput
}
