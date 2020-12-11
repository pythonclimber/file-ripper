enum class FileType {
    DELIMITED,
    FIXED,
    XML
}

class FileDefinition {
    lateinit var fileMask: String
    lateinit var fileType: FileType
    var hasHeader: Boolean = false
    lateinit var delimiter: String
    lateinit var fieldDefinitions: MutableList<FieldDefinition>
    lateinit var recordXmlElement: String
    lateinit var inputDirectory: String
    var completedDirectory: String? = null
}

class FieldDefinition {
    lateinit var fieldName: String
    var startPosition: Int? = null
    var fieldLength: Int? = null
    var positionInRow: Int? = null
    lateinit var xmlFieldName: String
}