package com.study_kotlin.backend.infrastructure.aws

import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.net.URI

@Component
class KinesisClientFactory(private val kinesisConfig: KinesisConfig) {
    fun create(): KinesisAsyncClient {
        val builder = KinesisAsyncClient.builder()
            .region(Region.of(kinesisConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        kinesisConfig.accessKey,
                        kinesisConfig.secretKey
                    )
                )
            )

        kinesisConfig.endpoint.let {
            builder.endpointOverride(URI.create(it))
        }

        return builder.build()
    }
}
