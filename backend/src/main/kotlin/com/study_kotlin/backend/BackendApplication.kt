package com.study_kotlin.backend

import com.study_kotlin.backend.infrastructure.SampleRecordProcessorFactory
import com.study_kotlin.backend.infrastructure.aws.KinesisClientFactory
import com.study_kotlin.backend.infrastructure.aws.DynamoDbClientFactory
import com.study_kotlin.backend.infrastructure.aws.CloudWatchClientFactory
import com.study_kotlin.backend.infrastructure.aws.KinesisConfig
import com.study_kotlin.backend.infrastructure.db.DbConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import java.util.UUID
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import software.amazon.kinesis.leases.LeaseManagementConfig

@SpringBootApplication
@ConfigurationPropertiesScan
class BackendApplication @Autowired constructor(
    private val kinesisClientFactory: KinesisClientFactory,
    private val dynamoDbClientFactory: DynamoDbClientFactory,
    private val cloudWatchClientFactory: CloudWatchClientFactory,
    private val dbConfig: DbConfig,
    private val kinesisConfig: KinesisConfig
) {
    val applicationName = "sample-kcl"
    val leaseTableName = "sample-kcl-lease"

    fun createScheduler(): Scheduler {
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
        return Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            leaseManagementConfig,
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig()
        )
    }

    // Kinesisデータの処理メソッド
    fun run() {
        // スケジューラの設定と開始
        val scheduler = createScheduler()
        val schedulerThread = Thread(scheduler)
        schedulerThread.isDaemon = true
        schedulerThread.start()
    }
}

fun main(args: Array<String>) {
    val applicationContext = runApplication<BackendApplication>(*args)
    val backendApplication = applicationContext.getBean(BackendApplication::class.java)
    backendApplication.run()
}
