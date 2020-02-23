package com.ohgnarly.fileripper.movers

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@FunctionalInterface
interface FileMover {
    fun moveFiles(files: List<File>, completedDirectory: String)
}

class DefaultFileMover : FileMover {
    override fun moveFiles(files: List<File>, completedDirectory: String) {
        val completedPath = Paths.get(completedDirectory)
        for (file in files) {
            Files.move(file.toPath(), completedPath)
        }
    }
}