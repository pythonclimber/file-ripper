package com.ohgnarly.fileripper.testhelpers

import kotlin.test.assertEquals

fun assertFileRecords(records: List<Map<String, Any>>) {
    assertFileRecord(records[0], "Aaron", "39", "09/04/1980")
    assertFileRecord(records[1], "Gene", "61", "01/15/1958")
    assertFileRecord(records[2], "Alexander", "4", "11/22/2014")
    assertFileRecord(records[3], "Mason", "12", "04/13/2007")
}

fun assertFileRecord(record: Map<String, Any>, name: String, age: String, dob: String) {
    assertEquals(name, record["name"])
    assertEquals(age, record["age"])
    assertEquals(dob, record["dob"])
}