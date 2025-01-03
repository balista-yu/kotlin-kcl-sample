package com.study_kotlin.backend.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("kinesis")
data class KinesisConfig (
    val endpoint: String,
    val region: String,
    val streamName: String,
    val accessKey: String,
    val secretKey: String
)
