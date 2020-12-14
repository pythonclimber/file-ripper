package com.ohgnarly.fileripper

abstract class FileDefinitionValidator {
    abstract fun isDefinitionValid(): Boolean
}

class DelimitedDefinitionValidator : FileDefinitionValidator() {
    override fun isDefinitionValid(): Boolean {
        println("Validating delimited definition")
        return true
    }
}

class FixedDefinitionValidator : FileDefinitionValidator() {
    override fun isDefinitionValid(): Boolean {
        println("Validating fixed definition")
        return true
    }
}

class XmlDefinitionValidator : FileDefinitionValidator() {
    override fun isDefinitionValid(): Boolean {
        println("Validating xml definition")
        return true
    }
}


