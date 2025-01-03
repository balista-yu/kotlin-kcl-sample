package com.study_kotlin.backend.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cloudwatch")
data class CloudWatchConfig (
    val endpoint: String
)
