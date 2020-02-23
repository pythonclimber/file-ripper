package com.ohgnarly.fileripper.testhelpers

fun buildPerson(fields: Map<String, Any>): Person {
    val person = Person()
    person.name = fields["name"].toString()
    person.age = fields["age"].toString()
    person.dob = fields["dob"].toString()
    return person
}