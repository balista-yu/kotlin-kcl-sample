package com.kotlin_kcl_sample.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan
class SampleApplication

fun main(args: Array<String>) {
    runApplication<SampleApplication>(*args)
}
