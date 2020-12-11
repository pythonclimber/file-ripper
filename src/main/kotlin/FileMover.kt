import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

interface FileMover {
    fun moveFiles(files: List<File>, completedDirectory: String)
}

class DefaultFileMover : FileMover {
    override fun moveFiles(files: List<File>, completedDirectory: String) {
        for (file in files) {
            val completedPath = Paths.get(completedDirectory, file.name)
            Files.move(file.toPath(), completedPath)
        }
    }
}