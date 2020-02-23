package com.ohgnarly.fileripper

import com.ohgnarly.fileripper.factories.createFileService
import com.ohgnarly.fileripper.models.FileDefinition
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.models.FileResult
import com.ohgnarly.fileripper.repositories.FileRepository
import com.ohgnarly.fileripper.repositories.WildcardFileRepository
import com.ohgnarly.fileripper.movers.DefaultFileMover
import com.ohgnarly.fileripper.movers.FileMover
import com.ohgnarly.fileripper.services.FileService
import org.springframework.stereotype.Component
import java.io.File
import java.util.stream.Collectors.toList

@Component
class FileRipper(private val fileRepository: FileRepository, private val fileMover: FileMover) {
    private var serviceFactory: (FileDefinition) -> FileService = ::createFileService

    constructor() : this(WildcardFileRepository(), DefaultFileMover())

    constructor(fileRepository: FileRepository) : this(fileRepository, DefaultFileMover())

    constructor(fileMover: FileMover) : this(WildcardFileRepository(), fileMover)

    internal constructor(fileRepository: FileRepository, serviceFactory: (FileDefinition) -> FileService,
                         fileMover: FileMover) : this(fileRepository, fileMover) {
        this.serviceFactory = serviceFactory
    }

    fun ripFile(file: File, fileDefinition: FileDefinition): FileOutput {
        val fileService = serviceFactory(fileDefinition)
        return fileService.processFile(file)
    }

    fun <T> ripFile(file: File, fileDefinition: FileDefinition,
                    recordBuilder: (Map<String, String>) -> T): FileResult<T> {
        val fileOutput = ripFile(file, fileDefinition)
        val fileResult = FileResult<T>(fileOutput.fileName)

        fileResult.records = fileOutput.records
                .stream()
                .map { r -> recordBuilder(r) }
                .collect(toList())

        return fileResult
    }

    fun ripFiles(files: List<File>, fileDefinition: FileDefinition): List<FileOutput> {
        return files
                .stream()
                .map { f -> ripFile(f, fileDefinition) }
                .collect(toList())
    }

    fun <T> ripFiles(files: List<File>, fileDefinition: FileDefinition,
                     recordBuilder: (Map<String, String>) -> T): List<FileResult<T>> {
        return files
                .stream()
                .map { f -> ripFile(f, fileDefinition, recordBuilder) }
                .collect(toList())
    }

    fun findAndRipFiles(fileDefinition: FileDefinition): List<FileOutput> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        val outputs = ripFiles(files, fileDefinition)
        fileMover.moveFiles(files, fileDefinition.completedDirectory)
        return outputs
    }

    fun <T> findAndRipFiles(fileDefinition: FileDefinition, recordBuilder: (Map<String, String>) -> T):
            List<FileResult<T>> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        val results = ripFiles(files, fileDefinition, recordBuilder)
        fileMover.moveFiles(files, fileDefinition.completedDirectory)
        return results
    }
}