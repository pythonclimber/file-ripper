package com.ohgnarly.fileripper.repositories

import org.apache.commons.io.filefilter.WildcardFileFilter
import java.io.File
import java.io.FileFilter

interface FileRepository {
    fun getFiles(inputDirectory: String, fileMask: String): List<File>
}

class WildcardFileRepository : FileRepository {
    override fun getFiles(inputDirectory: String, fileMask: String): List<File> {
        val fileFilter: FileFilter = WildcardFileFilter(fileMask)
        return File(inputDirectory).listFiles(fileFilter)!!.toMutableList()
    }
}