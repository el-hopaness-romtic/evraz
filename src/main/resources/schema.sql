CREATE TABLE IF NOT EXISTS general_info(
  id BIGSERIAL PRIMARY KEY,
  moment TIMESTAMP NOT NULL,
  properties text NOT NULL
);

CREATE TABLE IF NOT EXISTS exgauster_info(
  id BIGINT REFERENCES general_info(id),
  exgauster_number smallint NOT NULL,
  properties text NOT NULL
);

CREATE INDEX IF NOT EXISTS general_info_moment_brin on general_info using brin(moment);
