package com.kotlin_kcl_sample.app.infrastructure.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("dynamodb")
data class DynamoDbConfig (
    val endpoint: String
)
