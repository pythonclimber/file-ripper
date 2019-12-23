package com.ohgnarly.fileripper

import org.apache.commons.lang3.StringUtils.isNotBlank
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.lang.System.getenv
import java.util.Collections.singletonMap

@SpringBootApplication
open class FileRipperApplication

fun main(args: Array<String>) {
    val app = SpringApplication(FileRipperApplication::class.java)
    val port = if (isNotBlank(getenv("PORT")))
        getenv("PORT")
    else
        "3000"
    app.setDefaultProperties(singletonMap<String, Any>("server.port", port))
    app.run(*args)
}

