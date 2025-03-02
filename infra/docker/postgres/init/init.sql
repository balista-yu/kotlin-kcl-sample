DO
$$
BEGIN
    IF
EXISTS (SELECT FROM pg_database WHERE datname = 'kotlin_kcl_sample_test') THEN
        PERFORM pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'kotlin_kcl_sample_test';
        DROP
DATABASE kotlin_kcl_sample_test;
END IF;
END
$$;

CREATE DATABASE kotlin_kcl_sample_test
    WITH OWNER = 'test'
    ENCODING = 'UTF8'
    LC_COLLATE = 'ja_JP.UTF-8'
    LC_CTYPE = 'ja_JP.UTF-8'
    TEMPLATE = template0;

\c kotlin_kcl_sample_test;

CREATE TABLE IF NOT EXISTS kinesis_animals_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kinesis_foods_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\c kotlin_kcl_sample;

CREATE TABLE IF NOT EXISTS kinesis_animals_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kinesis_foods_data (
    id SERIAL PRIMARY KEY,
    partition_key VARCHAR(256) NOT NULL,
    sequence_number VARCHAR(256) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
