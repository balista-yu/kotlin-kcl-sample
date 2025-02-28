package com.study_kotlin.backend.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.study_kotlin.backend.infrastructure.db.DbConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import software.amazon.kinesis.exceptions.InvalidStateException
import software.amazon.kinesis.exceptions.KinesisClientLibRetryableException
import software.amazon.kinesis.exceptions.ShutdownException
import software.amazon.kinesis.lifecycle.events.InitializationInput
import software.amazon.kinesis.lifecycle.events.LeaseLostInput
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput
import software.amazon.kinesis.lifecycle.events.ShardEndedInput
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput
import software.amazon.kinesis.processor.ShardRecordProcessor
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import org.springframework.stereotype.Component

@Component
class SampleRecordProcessor (private val dbConfig: DbConfig) : ShardRecordProcessor {
    companion object {
        private const val SHARD_ID_MDC_KEY = "ShardId"
        private val log: Logger = LoggerFactory.getLogger(SampleRecordProcessor::class.java)
    }

    private var shardId: String? = null

    override fun initialize(initializationInput: InitializationInput) {
        shardId = initializationInput.shardId()

        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber())
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Processing {} record(s)", processRecordsInput.records().size)
            processRecordsInput.records().forEach { record ->
                val partitionKey = record.partitionKey()
                val sequenceNumber = record.sequenceNumber()
                val byteArray = ByteArray(record.data().remaining())
                record.data().get(byteArray)
                val data = String(byteArray)
                log.info("Processing record pk: {} -- Seq: {}", partitionKey, sequenceNumber)

                // PostgreSQL にデータを保存
                saveToDatabase(partitionKey, sequenceNumber, data)
            }

            // record check point
            processRecordsInput.checkpointer().checkpoint()
        } catch (e: KinesisClientLibRetryableException) {
            log.error("Caught throwable while processing records. Aborting.", e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    private fun saveToDatabase(partitionKey: String, sequenceNumber: String, data: String) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        val objectMapper = jacksonObjectMapper()
        val dynamoDbEvent: DynamoDbEvent = objectMapper.readValue(data)
        val isAnimal = dynamoDbEvent.tableName == "animals"

        try {
            connection = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password)
            val sql = if (isAnimal) "INSERT INTO kinesis_animals_data (partition_key, sequence_number, data) VALUES (?, ?, ?)" else "INSERT INTO kinesis_foods_data (partition_key, sequence_number, data) VALUES (?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, partitionKey)
            preparedStatement.setString(2, sequenceNumber)
            preparedStatement.setString(3, data)

            val rowsInserted = preparedStatement.executeUpdate()
            if (rowsInserted > 0) {
                log.info("Record successfully inserted into database.")
            }
        } catch (e: Exception) {
            log.error("Error inserting record into database.", e)
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Lost lease, so terminating.")
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Reached shard end checkpointing.")
            shardEndedInput.checkpointer().checkpoint()
        } catch (e: ShutdownException) {
            log.error("Exception while checkpointing at shard end. Giving up." ,e)
        } catch (e: InvalidStateException) {
            log.error("Exception while checkpointing at shard end. Giving up." ,e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Scheduler is shutting down, checkpointing.")
            shutdownRequestedInput.checkpointer().checkpoint()
        } catch (e: ShutdownException) {
            log.error("Exception while checkpointing at requested shutdown. Giving up." ,e)
        } catch (e: InvalidStateException) {
            log.error("Exception while checkpointing at requested shutdown. Giving up." ,e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }
}
