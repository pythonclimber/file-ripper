package com.ohgnarly.fileripper

import java.io.File
import java.nio.file.Files
import java.nio.file.Files.*
import java.nio.file.Paths
import java.nio.file.Paths.*

interface FileMover {
    fun moveFiles(files: List<File>, completedDirectory: String)
}

class DefaultFileMover : FileMover {
    override fun moveFiles(files: List<File>, completedDirectory: String) {
        if (!exists(get(completedDirectory))) {
            createDirectory(get(completedDirectory))
        }

        for (file in files) {
            move(file.toPath(), get(completedDirectory, file.name))
        }
    }
}