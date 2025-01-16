#!/bin/bash

# create dynamodb
awslocal dynamodb create-table \
    --table-name animals \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

awslocal dynamodb create-table \
    --table-name foods \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

# create dynamodb test用
awslocal dynamodb create-table \
    --table-name animals_test \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

awslocal dynamodb create-table \
    --table-name foods_test \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

# create kinesis stream
awslocal kinesis create-stream \
    --stream-name animals-stream \
    --shard-count 1 \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

awslocal kinesis create-stream \
    --stream-name foods-stream \
    --shard-count 1 \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

# create kinesis stream test用
awslocal kinesis create-stream \
    --stream-name animals-stream-test \
    --shard-count 1 \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

awslocal kinesis create-stream \
    --stream-name foods-stream-test \
    --shard-count 1 \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

# activate dynamodb kinesis data stream
awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name animals \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/animals-stream

awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name foods \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/foods-stream

# activate dynamodb kinesis data stream test用
awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name animals_test \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/animals-stream-test

awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name foods_test \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/foods-stream-test
