DO
$$
BEGIN
    IF
EXISTS (SELECT FROM pg_database WHERE datname = 'kotlin2_test') THEN
        PERFORM pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'kotlin2_test';
        DROP
DATABASE kotlin2_test;
END IF;
END
$$;

CREATE DATABASE kotlin2_test
    WITH OWNER = 'test'
    ENCODING = 'UTF8'
    LC_COLLATE = 'ja_JP.UTF-8'
    LC_CTYPE = 'ja_JP.UTF-8'
    TEMPLATE = template0;
