package com.kotlin_kcl_sample.app.infrastructure

import com.kotlin_kcl_sample.app.infrastructure.db.DbConfig
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

class SampleRecordProcessorFactory (private val dbConfig: DbConfig) : ShardRecordProcessorFactory {

    override fun shardRecordProcessor(): ShardRecordProcessor {
        return SampleRecordProcessor(dbConfig)
    }
}
