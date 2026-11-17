DROP TABLE IF EXISTS findings;
DROP TABLE IF EXISTS certificates;
DROP TABLE IF EXISTS vessels;
CREATE TABLE vessels (
  id BIGINT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  imo_number VARCHAR(30) NOT NULL UNIQUE,
  destination_port VARCHAR(100) NOT NULL,
  arrival_date DATE NOT NULL
);
CREATE TABLE certificates (
  id BIGINT PRIMARY KEY,
  vessel_id BIGINT NOT NULL REFERENCES vessels(id),
  certificate_type VARCHAR(120) NOT NULL,
  expiry_date DATE NOT NULL
);
CREATE TABLE findings (
  id BIGINT PRIMARY KEY,
  vessel_id BIGINT NOT NULL REFERENCES vessels(id),
  description VARCHAR(255) NOT NULL,
  status VARCHAR(10) NOT NULL CHECK (status IN ('OPEN','RESOLVED'))
);
CREATE INDEX idx_certificates_vessel ON certificates(vessel_id);
CREATE INDEX idx_findings_vessel ON findings(vessel_id);
