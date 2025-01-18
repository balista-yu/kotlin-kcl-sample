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
# awslocal dynamodb put-item --table-name animals --item '{"id": {"S": "hoge"}}'
# awslocal dynamodb put-item --table-name foods --item '{"id": {"S": "fuga"}}'
```

4. Show PostgreSQL tables
- You can confirm that an item PUT into the animals table in DynamoDB is registered in the kinesis_animals_data table in the database. 
- Similarly, you can confirm that an item PUT into the foods table in DynamoDB is registered in the kinesis_foods_data table in the database.
