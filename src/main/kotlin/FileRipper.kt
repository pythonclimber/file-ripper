import org.apache.commons.lang3.StringUtils.isNotBlank
import java.io.File
import java.util.stream.Collectors.toList

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

    /**
     * Rip a file and return results as a Map<String, String>
     * @param file File object to be ripped
     * @param fileDefinition the file's configuration
     * @return The data associated with the file.
     */
    fun ripFile(file: File, fileDefinition: FileDefinition): FileOutput {
        val fileService = serviceFactory(fileDefinition)
        return fileService.processFile(file)
    }

    /**
     * Rip a file and return results as a List<T>
     * @param T the type you want your file rows returned as
     * @param file File object to be ripped
     * @param fileDefinition the file's configuration
     * @param recordBuilder function of type (Map<String, String> -> T) to build return individual
     * response records
     * @return The data associated with the file.
     */
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

    /**
     * Rip multiple file and return results as a Map<String, String>
     * @param files list of File objects to be ripped
     * @param fileDefinition the file's configuration
     * @return The data associated with the file.
     */
    fun ripFiles(files: List<File>, fileDefinition: FileDefinition): List<FileOutput> {
        val outputList = files.map { f -> ripFile(f, fileDefinition) }

        if (isNotBlank(fileDefinition.completedDirectory)) {
            fileMover.moveFiles(files, fileDefinition.completedDirectory!!)
        }

        return outputList
    }

    /**
     * Rip multiple files and return results as a List<T>
     * @param T the type you want your file rows returned as
     * @param files list of File objects to be ripped
     * @param fileDefinition the file's configuration
     * @param recordBuilder function of type (Map<String, String> -> T) to build return individual
     * response records
     * @return The data associated with the file.
     */
    fun <T> ripFiles(files: List<File>, fileDefinition: FileDefinition,
                     recordBuilder: (Map<String, String>) -> T): List<FileResult<T>> {
        val resultList = files.map { f -> ripFile(f, fileDefinition, recordBuilder) }

        if (isNotBlank(fileDefinition.completedDirectory)) {
            fileMover.moveFiles(files, fileDefinition.completedDirectory!!)
        }

        return resultList
    }

    /**
     * Find files, rip them, and return results as a Map<String, String>
     * @param files list of File objects to be ripped
     * @param fileDefinition the file's configuration
     * @return The data associated with the file.
     */
    fun findAndRipFiles(fileDefinition: FileDefinition): List<FileOutput> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        return ripFiles(files, fileDefinition)
    }

    /**
     * Find files, rip them, and return results as a List<T>
     * @param T the type you want your file rows returned as
     * @param fileDefinition the file's configuration with must include an inputDirectory
     * @param recordBuilder function of type (Map<String, String> -> T) to build return individual
     * response records
     * @return The data associated with the file.
     */
    fun <T> findAndRipFiles(fileDefinition: FileDefinition, recordBuilder: (Map<String, String>) -> T):
            List<FileResult<T>> {
        val files = fileRepository.getFiles(fileDefinition.inputDirectory, fileDefinition.fileMask)
        return ripFiles(files, fileDefinition, recordBuilder)
    }
}

class FileOutput {
    var fileName: String = ""
    var records: List<Map<String, String>> = mutableListOf()
}

class FileResult<T>(var fileName: String) {
    var records: MutableList<T> = mutableListOf()
}

class FileRipperException : Exception {
    constructor(message: String) : super(message) {}

    constructor(cause: Throwable) : super(cause) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}