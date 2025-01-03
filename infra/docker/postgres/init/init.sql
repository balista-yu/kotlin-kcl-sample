DO
$$
BEGIN
    IF
EXISTS (SELECT FROM pg_database WHERE datname = 'study_kotlin_test') THEN
        PERFORM pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'study_kotlin_test';
        DROP
DATABASE study_kotlin_test;
END IF;
END
$$;

CREATE DATABASE study_kotlin_test
    WITH OWNER = 'test'
    ENCODING = 'UTF8'
    LC_COLLATE = 'ja_JP.UTF-8'
    LC_CTYPE = 'ja_JP.UTF-8'
    TEMPLATE = template0;

\c study_kotlin_test;

CREATE TABLE IF NOT EXISTS kinesis_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\c study_kotlin;

CREATE TABLE IF NOT EXISTS kinesis_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
