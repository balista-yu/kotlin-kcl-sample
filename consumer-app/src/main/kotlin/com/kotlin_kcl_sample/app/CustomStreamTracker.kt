package com.kotlin_kcl_sample.app

import com.kotlin_kcl_sample.app.infrastructure.aws.KinesisConfig
import software.amazon.awssdk.arns.Arn
import software.amazon.kinesis.common.InitialPositionInStream
import software.amazon.kinesis.common.InitialPositionInStreamExtended
import software.amazon.kinesis.common.StreamConfig
import software.amazon.kinesis.common.StreamIdentifier
import software.amazon.kinesis.processor.FormerStreamsLeasesDeletionStrategy
import software.amazon.kinesis.processor.MultiStreamTracker

class CustomStreamTracker(private val kinesisConfig: KinesisConfig): MultiStreamTracker {
    override fun streamConfigList(): List<StreamConfig?>? {

        return listOf(
            StreamConfig(
                StreamIdentifier.multiStreamInstance(Arn.fromString(kinesisConfig.animalsStreamName), 1673910400L ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON)
            ),
            StreamConfig(
                StreamIdentifier.multiStreamInstance(Arn.fromString(kinesisConfig.foodsStreamName), 1673910400L ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON)
            ),
        )
    }

    override fun formerStreamsLeasesDeletionStrategy(): FormerStreamsLeasesDeletionStrategy? {
        return FormerStreamsLeasesDeletionStrategy.NoLeaseDeletionStrategy()
    }
}
