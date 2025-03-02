package com.kotlin_kcl_sample.app.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cloudwatch")
data class CloudWatchConfig (
    val endpoint: String
)
