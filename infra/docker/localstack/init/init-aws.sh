#!/bin/bash

create_dynamodb_table() {
    local table_name=$1
    awslocal dynamodb create-table \
        --attribute-definitions \
            AttributeName=id,AttributeType=S \
        --table-name "$table_name" \
        --key-schema \
            AttributeName=id,KeyType=HASH \
        --billing-mode=PAY_PER_REQUEST \
        --no-deletion-protection-enabled
}

create_kinesis_stream() {
    local stream_name=$1
    awslocal kinesis create-stream \
        --stream-name "$stream_name" \
        --stream-mode-details '{"StreamMode": "ON_DEMAND"}'
}

enable_dynamodb_kinesis_stream() {
    local table_name=$1
    local stream_name=$2
    awslocal dynamodb enable-kinesis-streaming-destination \
        --table-name "$table_name" \
        --stream-arn "arn:aws:kinesis:ap-northeast-1:000000000000:stream/$stream_name"
}

create_dynamodb_table "animals"
create_dynamodb_table "foods"

create_kinesis_stream "animals-stream"
create_kinesis_stream "foods-stream"

enable_dynamodb_kinesis_stream "animals" "animals-stream"
enable_dynamodb_kinesis_stream "foods" "foods-stream"

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
