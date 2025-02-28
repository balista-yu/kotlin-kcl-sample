package com.study_kotlin.backend

import com.study_kotlin.backend.infrastructure.SampleRecordProcessorFactory
import com.study_kotlin.backend.infrastructure.aws.KinesisClientFactory
import com.study_kotlin.backend.infrastructure.aws.DynamoDbClientFactory
import com.study_kotlin.backend.infrastructure.aws.CloudWatchClientFactory
import com.study_kotlin.backend.infrastructure.aws.KinesisConfig
import com.study_kotlin.backend.infrastructure.db.DbConfig
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import java.util.UUID
import org.springframework.stereotype.Component
import software.amazon.kinesis.leases.LeaseManagementConfig

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
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
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
        scope.launch {
            scheduler.run()
        }
    }

    @PreDestroy
    fun shutdown() {
        println("Shutting down Kinesis consumer...")
        scheduler.shutdown()
        scope.cancel()
    }
}

