package com.ohgnarly.fileripper.controllers

import com.ohgnarly.fileripper.exceptions.FileRipperException
import com.ohgnarly.fileripper.factories.FileServiceFactory
import com.ohgnarly.fileripper.factories.FileServiceFactoryImpl
import com.ohgnarly.fileripper.models.FileOutput
import com.ohgnarly.fileripper.models.FileRipperRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@RestController
class FileRipperController @Autowired
constructor(private val fileServiceFactory: FileServiceFactory) {

    constructor() : this(FileServiceFactoryImpl()) {}

    @PostMapping(value = ["/rip"], consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    @Throws(FileRipperException::class)
    fun getFileOutput(request: FileRipperRequest): ResponseEntity<FileOutput> {
        val fileService = fileServiceFactory.createFileService(request.fileDefinition!!)
        val fileOutput = fileService.processFile(convertFile(request.multipartFile!!))
        fileOutput.fileName = request.multipartFile!!.name
        return ResponseEntity(fileOutput, OK)
    }

    @Throws(FileRipperException::class)
    private fun convertFile(multipartFile: MultipartFile): File {
        try {
            val file = File(multipartFile.name)
            file.createNewFile()
            val fos = FileOutputStream(file)
            fos.write(multipartFile.bytes)
            fos.close()
            return file
        } catch (ex: IOException) {
            throw FileRipperException(ex)
        }

    }
}
