package com.kotlin_kcl_sample.app.infrastructure.db

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("postgres")
data class DbConfig (
    val url: String,
    val username: String,
    val password: String
)
