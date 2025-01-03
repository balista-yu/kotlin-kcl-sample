package com.study_kotlin.backend.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("dynamodb")
data class DynamoDbConfig (
    val endpoint: String,
    val region: String,
    val accessKey: String,
    val secretKey: String,
)
