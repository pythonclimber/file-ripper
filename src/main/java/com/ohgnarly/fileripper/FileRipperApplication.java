package com.ohgnarly.fileripper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

import static java.lang.System.getenv;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@SpringBootApplication
public class FileRipperApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FileRipperApplication.class);
        String port = isNotBlank(getenv("PORT"))
                ? getenv("PORT")
                : "3000";
        app.setDefaultProperties(singletonMap("server.port", port));
        app.run(args);
    }
}
