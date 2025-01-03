# Name

Study kotlin 

## Overview

This is kotlin app sample.

## Getting Start

1. Clone the repository

```
$ git clone https://github.com/balista-yu/study-kotlin.git
```

2. Run docker compose
```
$ cd study-kotlin
$ task up
```

3. Create Table with PostgresSQL
```sql
CREATE TABLE kinesis_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

4. DynamoDB Insert Record with localstack
```
# awslocal dynamodb put-item --table-name study_kotlin --item '{"id": {"S": "hoge"}}'
```
