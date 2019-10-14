package com.ohgnarly.fileripper.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

import static io.cucumber.junit.CucumberOptions.SnippetType.CAMELCASE;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"@FileRipper"},
        strict = true,
        features = {"src/test/resources/features"},
        plugin = {"pretty", "junit:target/cucumber/report.xml"},
        snippets = CAMELCASE
)
public class FileRipperCukeRunner {
}
