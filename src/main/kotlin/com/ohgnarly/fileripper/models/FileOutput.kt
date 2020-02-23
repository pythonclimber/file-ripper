package com.ohgnarly.fileripper.models

interface Record {
    fun setProperties(fieldMap: Map<String, Any>)
}

class FileOutput {
    var fileName: String = ""
    var records: List<Map<String, String>> = mutableListOf()
}

class FileResult<T>(var fileName: String) {
    var records: MutableList<T> = mutableListOf()
}