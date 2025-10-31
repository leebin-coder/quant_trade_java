-- User Service Database Schema
-- V1: Initialize user tables

-- Users table
CREATE TABLE IF NOT EXISTS users (
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
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_mobile ON users(mobile) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Comments
COMMENT ON TABLE users IS 'User accounts table';
COMMENT ON COLUMN users.id IS 'Primary key';
COMMENT ON COLUMN users.username IS 'Unique username';
COMMENT ON COLUMN users.email IS 'User email address';
COMMENT ON COLUMN users.mobile IS 'User mobile phone number';
COMMENT ON COLUMN users.password IS 'Encrypted password';
COMMENT ON COLUMN users.status IS 'User status: ACTIVE, INACTIVE, LOCKED';
COMMENT ON COLUMN users.deleted IS 'Soft delete flag';
