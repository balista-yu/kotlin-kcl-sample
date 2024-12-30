#!/bin/bash

# create dynamodb
awslocal dynamodb create-table \
    --table-name study_kotlin \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

# create dynamodb test用
awslocal dynamodb create-table \
    --table-name study_kotlin_test \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

# create kinesis stream
awslocal kinesis create-stream \
    --stream-name study-kotlin-stream \
    --shard-count 1

# create kinesis stream test用
awslocal kinesis create-stream \
    --stream-name study-kotlin-stream-test \
    --shard-count 1

# activate dynamodb kinesis data stream
awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name study_kotlin \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/study-kotlin-stream \
    --enable-kinesis-streaming-configuration ApproximateCreationDateTimePrecision=MICROSECOND

# activate dynamodb kinesis data stream test用
awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name study_kotlin_test \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/study-kotlin-stream-test \
    --enable-kinesis-streaming-configuration ApproximateCreationDateTimePrecision=MICROSECOND
