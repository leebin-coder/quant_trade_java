-- User Service Database Schema
-- V1: Initialize user tables

-- Users table
CREATE TABLE IF NOT EXISTS t_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(128) UNIQUE,
    mobile VARCHAR(32) UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Index for common queries
CREATE INDEX IF NOT EXISTS idx_users_username ON t_user(username) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_email ON t_user(email) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_mobile ON t_user(mobile) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_status ON t_user(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_created_at ON t_user(created_at);

-- Comments
COMMENT ON TABLE t_user IS 'User accounts table';
COMMENT ON COLUMN t_user.id IS 'Primary key';
COMMENT ON COLUMN t_user.username IS 'Unique username';
COMMENT ON COLUMN t_user.email IS 'User email address';
COMMENT ON COLUMN t_user.mobile IS 'User mobile phone number';
COMMENT ON COLUMN t_user.password IS 'Encrypted password';
COMMENT ON COLUMN t_user.status IS 'User status: ACTIVE, INACTIVE, LOCKED';
COMMENT ON COLUMN t_user.deleted IS 'Soft delete flag';
