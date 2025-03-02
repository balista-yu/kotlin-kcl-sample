package com.kotlin_kcl_sample.app.infrastructure.aws

import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.net.URI

@Component
class KinesisClientFactory(
    private val awsConfig: AwsConfig,
    private val kinesisConfig: KinesisConfig
) {
    fun create(): KinesisAsyncClient {
        val builder = KinesisAsyncClient.builder()
            .region(Region.of(awsConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsConfig.accessKey,
                        awsConfig.secretKey
                    )
                )
            )

        kinesisConfig.endpoint.let {
            builder.endpointOverride(URI.create(it))
        }

        return builder.build()
    }
}
