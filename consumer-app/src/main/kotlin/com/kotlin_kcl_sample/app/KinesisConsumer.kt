package com.kotlin_kcl_sample.app

import com.kotlin_kcl_sample.app.infrastructure.SampleRecordProcessorFactory
import com.kotlin_kcl_sample.app.infrastructure.aws.KinesisClientFactory
import com.kotlin_kcl_sample.app.infrastructure.aws.DynamoDbClientFactory
import com.kotlin_kcl_sample.app.infrastructure.aws.CloudWatchClientFactory
import com.kotlin_kcl_sample.app.infrastructure.aws.KinesisConfig
import com.kotlin_kcl_sample.app.infrastructure.db.DbConfig
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import java.util.UUID
import org.springframework.stereotype.Component
import software.amazon.kinesis.leases.LeaseManagementConfig
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class KinesisConsumer @Autowired constructor(
    private val kinesisClientFactory: KinesisClientFactory,
    private val dynamoDbClientFactory: DynamoDbClientFactory,
    private val cloudWatchClientFactory: CloudWatchClientFactory,
    private val dbConfig: DbConfig,
    private val kinesisConfig: KinesisConfig
) {
    private val applicationName = "sample-kcl"
    private val leaseTableName = "sample-kcl-lease"
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var scheduler: Scheduler

    init {
        startKinesisConsumer()
    }

    private fun startKinesisConsumer() {
        val kinesisClient: KinesisAsyncClient = kinesisClientFactory.create()
        val dynamoDbAsyncClient: DynamoDbAsyncClient = dynamoDbClientFactory.create()
        val cloudWatchClient: CloudWatchAsyncClient = cloudWatchClientFactory.create()
        val workerIdentifier = UUID.randomUUID().toString()
        val customStreamTracker = CustomStreamTracker(kinesisConfig)

        // データ処理のための設定を作成
        val configsBuilder = ConfigsBuilder(
            customStreamTracker,
            applicationName,
            kinesisClient,
            dynamoDbAsyncClient,
            cloudWatchClient,
            workerIdentifier,
            SampleRecordProcessorFactory(dbConfig)
        )

        // KCLがローカル環境（localstack）でEC2メタデータサービスにアクセスしようとしているが、必要ないため無効化
        val leaseManagementConfig = LeaseManagementConfig(
            leaseTableName,
            applicationName,
            dynamoDbAsyncClient,
            kinesisClient,
            workerIdentifier
        ).workerUtilizationAwareAssignmentConfig(
            LeaseManagementConfig.WorkerUtilizationAwareAssignmentConfig()
                .disableWorkerMetrics(true).workerMetricsTableConfig(
                    LeaseManagementConfig.WorkerMetricsTableConfig(applicationName)
                ))

        // スケジューラの設定と開始
        scheduler = Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            leaseManagementConfig,
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig()
        )

        executorService.submit(scheduler)
    }

    @PreDestroy
    fun shutdown() {
        println("Shutting down Kinesis consumer...")
        scheduler.shutdown()
        executorService.shutdown()
    }
}

