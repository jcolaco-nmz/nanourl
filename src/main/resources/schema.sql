CREATE TABLE IF NOT EXISTS short_url (
    id BIGSERIAL PRIMARY KEY,
    url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS short_url_access_log (
    id BIGSERIAL PRIMARY KEY,
    short_url_id BIGINT not null,
    timestamp TIMESTAMP DEFAULT Now()
);
