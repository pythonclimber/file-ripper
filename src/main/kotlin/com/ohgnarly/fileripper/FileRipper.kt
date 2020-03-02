package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils.isNotBlank
import org.springframework.stereotype.Component
import java.io.File
import java.util.stream.Collectors.toList

@Component
class FileRipper(private val fileRepository: FileRepository, private val fileMover: FileMover,
                 private val dataExporter: DataExporter) {
    private var serviceFactory: (FileDefinition) -> FileService = (FileService)::create

    constructor() : this(WildcardFileRepository(), DefaultFileMover(), DefaultDataExporter())

    constructor(fileRepository: FileRepository) : this(fileRepository, DefaultFileMover(), DefaultDataExporter())

    constructor(fileMover: FileMover) : this(WildcardFileRepository(), fileMover, DefaultDataExporter())

    constructor(dataExporter: DataExporter) : this(WildcardFileRepository(), DefaultFileMover(), dataExporter)

    constructor(fileRepository: FileRepository, fileMover: FileMover) : this(fileRepository, fileMover, DefaultDataExporter())

    constructor(fileRepository: FileRepository, dataExporter: DataExporter) : this(fileRepository, DefaultFileMover(), dataExporter)

    constructor(fileMover: FileMover, dataExporter: DataExporter) : this(WildcardFileRepository(), fileMover, dataExporter)

    internal constructor(fileRepository: FileRepository, serviceFactory: (FileDefinition) -> FileService,
                         fileMover: FileMover) : this(fileRepository, fileMover, DefaultDataExporter()) {
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
        val outputList = files.map { f -> ripFile(f, fileDefinition) }

        if (isNotBlank(fileDefinition.completedDirectory)) {
            fileMover.moveFiles(files, fileDefinition.completedDirectory!!)
        }

        return outputList
    }

    fun <T> ripFiles(files: List<File>, fileDefinition: FileDefinition,
                     recordBuilder: (Map<String, String>) -> T): List<FileResult<T>> {
        val resultList = files.map { f -> ripFile(f, fileDefinition, recordBuilder) }

        if (isNotBlank(fileDefinition.completedDirectory)) {
            fileMover.moveFiles(files, fileDefinition.completedDirectory!!)
        }

        return resultList
    }

    fun findAndRipFiles(fileDefinition: FileDefinition): List<FileOutput> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        return ripFiles(files, fileDefinition)
    }

    fun <T> findAndRipFiles(fileDefinition: FileDefinition, recordBuilder: (Map<String, String>) -> T):
            List<FileResult<T>> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        return ripFiles(files, fileDefinition, recordBuilder)
    }
}