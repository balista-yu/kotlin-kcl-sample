package com.kotlin_kcl_sample.app.infrastructure.aws

import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.net.URI

@Component
class CloudWatchClientFactory(
    private val awsConfig: AwsConfig,
    private val cloudWatchConfig: CloudWatchConfig
) {
    fun create(): CloudWatchAsyncClient {
        val builder = CloudWatchAsyncClient.builder()
            .region(Region.of(awsConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsConfig.accessKey,
                        awsConfig.secretKey
                    )
                )
            )

        cloudWatchConfig.endpoint.let {
            builder.endpointOverride(URI.create(it))
        }

        return builder.build()
    }
}
