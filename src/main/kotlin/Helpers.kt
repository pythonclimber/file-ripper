class ValidationResult(private val validationMessages: List<String>) {
    fun isValid(): Boolean {
        return validationMessages.size <= 0
    }

    fun getValidationMessage(): String {
        return validationMessages.joinToString(separator = ", ")
    }
}