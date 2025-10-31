-- Risk Service Database Schema
-- V1: Initialize risk management tables

-- Risk rules table
CREATE TABLE IF NOT EXISTS risk_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(128) NOT NULL,
    rule_type VARCHAR(64) NOT NULL,
    rule_config TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Risk alerts table
CREATE TABLE IF NOT EXISTS risk_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(64) NOT NULL,
    severity VARCHAR(32) NOT NULL,
    user_id BIGINT,
    order_id BIGINT,
    alert_message TEXT NOT NULL,
    alert_data TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    resolved_at TIMESTAMP,
    resolved_by VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User risk limits table
CREATE TABLE IF NOT EXISTS user_risk_limits (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    max_position_size DECIMAL(20, 8),
    max_daily_loss DECIMAL(20, 8),
    max_order_size DECIMAL(20, 8),
    leverage_limit DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_risk_rules_enabled ON risk_rules(enabled) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_risk_rules_priority ON risk_rules(priority);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_status ON risk_alerts(status);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_user_id ON risk_alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_created_at ON risk_alerts(created_at);
CREATE INDEX IF NOT EXISTS idx_user_risk_limits_user_id ON user_risk_limits(user_id);

-- Comments
COMMENT ON TABLE risk_rules IS 'Risk control rules configuration';
COMMENT ON TABLE risk_alerts IS 'Risk alerts and violations';
COMMENT ON TABLE user_risk_limits IS 'User-specific risk limits';
COMMENT ON COLUMN risk_alerts.severity IS 'Alert severity: LOW, MEDIUM, HIGH, CRITICAL';
COMMENT ON COLUMN risk_alerts.status IS 'Alert status: OPEN, ACKNOWLEDGED, RESOLVED';
