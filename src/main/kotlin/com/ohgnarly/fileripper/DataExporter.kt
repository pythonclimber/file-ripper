package com.ohgnarly.fileripper

interface DataExporter {
    fun export(data: Map<String, String>) {}
    fun <T> export(data: T) {}
}

class DefaultDataExporter : DataExporter {}