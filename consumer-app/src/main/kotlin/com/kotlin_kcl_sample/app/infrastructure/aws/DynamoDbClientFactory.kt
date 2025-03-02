package com.kotlin_kcl_sample.app.infrastructure.aws

import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.net.URI

@Component
class DynamoDbClientFactory(
    private val awsConfig: AwsConfig,
    private val dynamoDbConfig: DynamoDbConfig
) {
    fun create(): DynamoDbAsyncClient {
        val builder = DynamoDbAsyncClient.builder()
            .region(Region.of(awsConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsConfig.accessKey,
                        awsConfig.secretKey
                    )
                )
            )

        dynamoDbConfig.endpoint.let {
            builder.endpointOverride(URI.create(it))
        }

        return builder.build()
    }
}
