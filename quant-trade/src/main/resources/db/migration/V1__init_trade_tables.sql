-- Trade Service Database Schema
-- V1: Initialize trade tables

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    side VARCHAR(32) NOT NULL,
    price DECIMAL(20, 8),
    quantity DECIMAL(20, 8) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    version INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Order executions table
CREATE TABLE IF NOT EXISTS order_executions (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    executed_price DECIMAL(20, 8) NOT NULL,
    executed_quantity DECIMAL(20, 8) NOT NULL,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_orders_symbol ON orders(symbol) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_executions_order_id ON order_executions(order_id);
CREATE INDEX IF NOT EXISTS idx_order_executions_executed_at ON order_executions(executed_at);

-- Comments
COMMENT ON TABLE orders IS 'Trading orders table';
COMMENT ON COLUMN orders.user_id IS 'User ID who placed the order';
COMMENT ON COLUMN orders.symbol IS 'Trading symbol (e.g., BTCUSDT)';
COMMENT ON COLUMN orders.type IS 'Order type: MARKET, LIMIT, STOP';
COMMENT ON COLUMN orders.side IS 'Order side: BUY, SELL';
COMMENT ON COLUMN orders.price IS 'Order price (NULL for market orders)';
COMMENT ON COLUMN orders.quantity IS 'Order quantity';
COMMENT ON COLUMN orders.status IS 'Order status: PENDING, FILLED, PARTIALLY_FILLED, CANCELLED, REJECTED';

COMMENT ON TABLE order_executions IS 'Order execution records';
COMMENT ON COLUMN order_executions.order_id IS 'Associated order ID';
COMMENT ON COLUMN order_executions.executed_price IS 'Execution price';
COMMENT ON COLUMN order_executions.executed_quantity IS 'Executed quantity';
