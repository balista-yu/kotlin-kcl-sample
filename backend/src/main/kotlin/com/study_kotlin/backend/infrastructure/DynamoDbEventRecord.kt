package com.study_kotlin.backend.infrastructure

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class DynamoDbEvent(
    val tableName: String,
    val recordFormat: String,
    val userIdentity: String?,
    val eventID: String,
    val dynamodb: DynamoDbDetail,
    val awsRegion: String,
    val eventSource: String,
    val eventName: String,
)

data class DynamoDbDetail(
    val ApproximateCreationDateTime: Long,
    val SizeBytes: Int,
    val Keys: Key,
    val NewImage: NewImage
)

data class Key(
    val id: AttributeValue
)

data class NewImage(
    val id: AttributeValue
)

data class AttributeValue @JsonCreator constructor(
    @JsonProperty("S") val S: String?
)
