package com.ohgnarly.fileripper

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        tags = ["@FileRipper"],
        strict = true,
        features = ["src/test/resources/features"],
        plugin = ["pretty", "junit:target/cucumber/report.xml"],
        snippets = CucumberOptions.SnippetType.CAMELCASE)
class RunCukesTest