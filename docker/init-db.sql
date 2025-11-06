-- Create databases
CREATE DATABASE quant_trade;
CREATE DATABASE nacos_config;

-- Switch to nacos_config database
\c nacos_config;

-- Nacos Database Schema for PostgreSQL
CREATE TABLE IF NOT EXISTS config_info (
  id BIGSERIAL PRIMARY KEY,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(255) DEFAULT NULL,
  content TEXT NOT NULL,
  md5 VARCHAR(32) DEFAULT NULL,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  src_user TEXT,
  src_ip VARCHAR(50) DEFAULT NULL,
  app_name VARCHAR(128) DEFAULT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  c_desc VARCHAR(256) DEFAULT NULL,
  c_use VARCHAR(64) DEFAULT NULL,
  effect VARCHAR(64) DEFAULT NULL,
  type VARCHAR(64) DEFAULT NULL,
  c_schema TEXT,
  encrypted_data_key TEXT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS config_info_aggr (
  id BIGSERIAL PRIMARY KEY,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(255) NOT NULL,
  datum_id VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  app_name VARCHAR(128) DEFAULT NULL,
  tenant_id VARCHAR(128) DEFAULT ''
);

CREATE TABLE IF NOT EXISTS config_info_beta (
  id BIGSERIAL PRIMARY KEY,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(255) NOT NULL,
  app_name VARCHAR(128) DEFAULT NULL,
  content TEXT NOT NULL,
  beta_ips VARCHAR(1024) DEFAULT NULL,
  md5 VARCHAR(32) DEFAULT NULL,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  src_user TEXT,
  src_ip VARCHAR(50) DEFAULT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  encrypted_data_key TEXT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS config_info_tag (
  id BIGSERIAL PRIMARY KEY,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(255) NOT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  tag_id VARCHAR(128) NOT NULL,
  app_name VARCHAR(128) DEFAULT NULL,
  content TEXT NOT NULL,
  md5 VARCHAR(32) DEFAULT NULL,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  src_user TEXT,
  src_ip VARCHAR(50) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS config_tags_relation (
  id BIGINT NOT NULL,
  tag_name VARCHAR(128) NOT NULL,
  tag_type VARCHAR(64) DEFAULT NULL,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(128) NOT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  nid BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS group_capacity (
  id BIGSERIAL PRIMARY KEY,
  group_id VARCHAR(128) NOT NULL DEFAULT '',
  quota INTEGER NOT NULL DEFAULT 0,
  usage INTEGER NOT NULL DEFAULT 0,
  max_size INTEGER NOT NULL DEFAULT 0,
  max_aggr_count INTEGER NOT NULL DEFAULT 0,
  max_aggr_size INTEGER NOT NULL DEFAULT 0,
  max_history_count INTEGER NOT NULL DEFAULT 0,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (group_id)
);

CREATE TABLE IF NOT EXISTS his_config_info (
  id BIGINT NOT NULL,
  nid BIGSERIAL PRIMARY KEY,
  data_id VARCHAR(255) NOT NULL,
  group_id VARCHAR(255) NOT NULL,
  app_name VARCHAR(128) DEFAULT NULL,
  content TEXT NOT NULL,
  md5 VARCHAR(32) DEFAULT NULL,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  src_user TEXT,
  src_ip VARCHAR(50) DEFAULT NULL,
  op_type CHAR(10) DEFAULT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  encrypted_data_key TEXT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS tenant_capacity (
  id BIGSERIAL PRIMARY KEY,
  tenant_id VARCHAR(128) NOT NULL DEFAULT '',
  quota INTEGER NOT NULL DEFAULT 0,
  usage INTEGER NOT NULL DEFAULT 0,
  max_size INTEGER NOT NULL DEFAULT 0,
  max_aggr_count INTEGER NOT NULL DEFAULT 0,
  max_aggr_size INTEGER NOT NULL DEFAULT 0,
  max_history_count INTEGER NOT NULL DEFAULT 0,
  gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (tenant_id)
);

CREATE TABLE IF NOT EXISTS tenant_info (
  id BIGSERIAL PRIMARY KEY,
  kp VARCHAR(128) NOT NULL,
  tenant_id VARCHAR(128) DEFAULT '',
  tenant_name VARCHAR(128) DEFAULT '',
  tenant_desc VARCHAR(256) DEFAULT NULL,
  create_source VARCHAR(32) DEFAULT NULL,
  gmt_create BIGINT NOT NULL,
  gmt_modified BIGINT NOT NULL,
  UNIQUE (kp, tenant_id)
);

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(50) NOT NULL PRIMARY KEY,
  password VARCHAR(500) NOT NULL,
  enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
  username VARCHAR(50) NOT NULL,
  role VARCHAR(50) NOT NULL,
  UNIQUE (username, role)
);

CREATE TABLE IF NOT EXISTS permissions (
  role VARCHAR(50) NOT NULL,
  resource VARCHAR(255) NOT NULL,
  action VARCHAR(8) NOT NULL,
  UNIQUE (role, resource, action)
);

-- Create indexes
CREATE INDEX idx_tenant_id ON config_info(tenant_id);
CREATE INDEX idx_config_info_aggr_group_id ON config_info_aggr(group_id);
CREATE INDEX idx_config_info_aggr_data_id ON config_info_aggr(data_id);
CREATE INDEX idx_config_info_aggr_tenant_id ON config_info_aggr(tenant_id);
CREATE INDEX idx_config_info_beta_tenant_id ON config_info_beta(tenant_id);
CREATE INDEX idx_config_info_tag_tenant_id ON config_info_tag(tenant_id);
CREATE INDEX idx_config_tags_relation_tenant_id ON config_tags_relation(tenant_id);
CREATE INDEX idx_his_config_info_gmt_create ON his_config_info(gmt_create);
CREATE INDEX idx_his_config_info_gmt_modified ON his_config_info(gmt_modified);
CREATE INDEX idx_his_config_info_did ON his_config_info(data_id);

-- Insert default admin user (username: nacos, password: nacos)
INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE) ON CONFLICT (username) DO NOTHING;
INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN') ON CONFLICT DO NOTHING;

-- Grant permissions to user libin
GRANT ALL PRIVILEGES ON DATABASE quant_trade TO libin;
GRANT ALL PRIVILEGES ON DATABASE nacos_config TO libin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO libin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO libin;
