package com.study_kotlin.backend.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("postgres")
data class DbConfig (
    val url: String,
    val username: String,
    val password: String
)
