-- formatter:off
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE person (
    id              uuid        PRIMARY KEY         DEFAULT     uuid_generate_v4(),
    login           text        NOT NULL,
    first_name      text        NOT NULL,
    last_name       text        NOT NULL,
    phone           text
);

CREATE TYPE status AS ENUM ('FAIL', 'SUCCESS', 'PENDING');
CREATE CAST (varchar AS status) WITH INOUT AS IMPLICIT;

CREATE TABLE upload_history (
    id                      uuid                        PRIMARY KEY         DEFAULT     uuid_generate_v4(),
    version                 int                         NOT NULL,
    application_name        text,
    filename                text,
    created_date            timestamp with time zone    NOT NULL,
    status                  status                      NOT NULL
);