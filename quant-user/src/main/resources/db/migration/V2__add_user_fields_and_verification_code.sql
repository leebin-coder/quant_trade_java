-- User Service Database Schema
-- V2: Add nick_name, sex fields and create verification_codes table

-- Modify users table - add new fields
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS nick_name VARCHAR(128);
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS sex VARCHAR(16);

-- Make password nullable for phone-based registration
ALTER TABLE t_user ALTER COLUMN password DROP NOT NULL;

-- Make username nullable and non-unique for phone-only registration
ALTER TABLE t_user ALTER COLUMN username DROP NOT NULL;
ALTER TABLE t_user DROP CONSTRAINT IF EXISTS users_username_key;

-- Ensure mobile is not null (primary identifier)
ALTER TABLE t_user ALTER COLUMN mobile SET NOT NULL;

-- Update comments
COMMENT ON COLUMN t_user.nick_name IS 'User nickname';
COMMENT ON COLUMN t_user.sex IS 'User gender: MALE, FEMALE, UNKNOWN';

-- Create verification codes table
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(32) NOT NULL,
    code VARCHAR(6) NOT NULL,
    type VARCHAR(32) NOT NULL DEFAULT 'LOGIN',
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP
);

-- Index for verification code queries
CREATE INDEX IF NOT EXISTS idx_verification_codes_phone ON verification_codes(phone);
CREATE INDEX IF NOT EXISTS idx_verification_codes_created_at ON verification_codes(created_at);
CREATE INDEX IF NOT EXISTS idx_verification_codes_expired_at ON verification_codes(expired_at);

-- Comments for verification_codes table
COMMENT ON TABLE verification_codes IS 'Verification codes table for SMS authentication';
COMMENT ON COLUMN verification_codes.id IS 'Primary key';
COMMENT ON COLUMN verification_codes.phone IS 'Mobile phone number';
COMMENT ON COLUMN verification_codes.code IS 'Verification code (6 digits)';
COMMENT ON COLUMN verification_codes.type IS 'Code type: LOGIN, REGISTER, RESET_PASSWORD';
COMMENT ON COLUMN verification_codes.is_used IS 'Whether the code has been used';
COMMENT ON COLUMN verification_codes.expired_at IS 'Expiration time';
COMMENT ON COLUMN verification_codes.used_at IS 'Time when the code was used';
