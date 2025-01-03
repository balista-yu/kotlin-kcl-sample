# Name

Kotlin KCL Sample

## Overview

- KCL(Kinesis Client Library)で Kotlin を使用したコンシューマーアプリのサンプル
- Kinesis Data Streams を使用して DynamoDB の変更をキャプチャ（CDC）し、RDSに変更データを登録

## Getting Start

1. Clone the repository

```
$ git clone https://github.com/balista-yu/kotlin-kcl-sample.git
```

2. Run docker compose
```
$ cd kotlin-kcl-sample
$ task up
```

3. DynamoDB Insert Record with localstack
```
# awslocal dynamodb put-item --table-name study_kotlin --item '{"id": {"S": "hoge"}}'
```
