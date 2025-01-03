package com.study_kotlin.backend.infrastructure

import com.study_kotlin.backend.infrastructure.db.DbConfig
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

class SampleRecordProcessorFactory (private val dbConfig: DbConfig) : ShardRecordProcessorFactory {

    override fun shardRecordProcessor(): ShardRecordProcessor {
        return SampleRecordProcessor(dbConfig)
    }
}
