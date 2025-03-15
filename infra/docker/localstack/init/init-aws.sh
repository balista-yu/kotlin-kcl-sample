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

# @see https://github.com/localstack/localstack/issues/10000
awslocal dynamodb create-table \
    --table-name sample-kcl-lease \
    --attribute-definitions \
        AttributeName=leaseOwner,AttributeType=S \
        AttributeName=leaseKey,AttributeType=S \
    --key-schema \
        AttributeName=leaseKey,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --global-secondary-indexes \
        '[
            {
                "IndexName": "LeaseOwnerToLeaseKeyIndex",
                "KeySchema": [
                    {
                        "AttributeName": "leaseOwner",
                        "KeyType": "HASH"
                    },
                    {
                        "AttributeName": "leaseKey",
                        "KeyType": "RANGE"
                    }
                ],
                "Projection": {
                    "ProjectionType": "KEYS_ONLY"
                }
            }
        ]'

awslocal dynamodb create-table \
    --table-name sample-kcl-WorkerMetricStats \
    --attribute-definitions \
        AttributeName=wid,AttributeType=S \
    --key-schema \
        AttributeName=wid,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST

awslocal dynamodb create-table \
    --table-name sample-kcl-CoordinatorState \
    --attribute-definitions \
        AttributeName=key,AttributeType=S \
    --key-schema \
        AttributeName=key,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST

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
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

awslocal kinesis create-stream \
    --stream-name foods-stream \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

# create kinesis stream test用
awslocal kinesis create-stream \
    --stream-name animals-stream-test \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

awslocal kinesis create-stream \
    --stream-name foods-stream-test \
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
